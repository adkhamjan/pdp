package uz.pdp.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import uz.pdp.bot.ECommerceBot;

import uz.pdp.model.Cart;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.Map;
import java.util.UUID;

public class CallbackHandlerService extends BotHandlerService {
    public CallbackHandlerService(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        super(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @SneakyThrows
    @Override

    public BotApiMethod<?> handler(Update update, ECommerceBot eCommerceBot) {

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long telUserId = update.getCallbackQuery().getFrom().getId();
            UUID userId = userIds.get(telUserId);
            UUID cartId = cartIdByUserId.get(userId);

            String data = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);

            String lang = languages.get(userId);
            Map<String, String> messages = LanguageBotService.getTexts(lang);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            String[] inlineProducts = {"-", "1", "+", messages.get("cart.save")};

            if (data.startsWith("LANGUAGE:")) {
                EditMessageText editMessageText1 = LanguageBotService.getEditMessageChangeLang(editMessageText, data, messages, languages, userId);
                saveLanguageToFile();
                return editMessageText1;
            } else if (data.startsWith("CATEGORY:")) {
                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE, PRODUCT_SERVICE);
                return categoryBotService.getEditMessageByCategory(data, editMessageText, messages);
            } else if (data.startsWith("PRODUCT:")) {
                ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE, CATEGORY_SERVICE);

                return productBotService.getEditMessageByProduct(editMessageText, data, inlineProducts, eCommerceBot);

            } else if (data.startsWith("NUMBER")) {
                ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE, CATEGORY_SERVICE);
                return productBotService.getEditMessageProductQuantity(editMessageText, data, inlineProducts, cartId, userId, CART_SERVICE);
            } else if (data.startsWith("ORDER:")) {
                return getEditMessageByOrder(editMessageText, data, userId, cartId, messages);
            }
            return editMessageText;
        }
        return null;
    }

    @SneakyThrows
    private EditMessageText getEditMessageByOrder(EditMessageText editMessageText, String data, UUID userId, UUID cartId, Map<String, String> messages) {
        String callback = data.split(":")[1];
        if (callback.equals(messages.get("cart.order"))) {
            Cart cart = CART_SERVICE.getCartByCartId(cartId);
            CART_SERVICE.addCartToOrders(cart);
            cartId = UUID.randomUUID();
            cartIdByUserId.put(userId, cartId);
            saveToFile();

            editMessageText.setText(messages.get("order.placed"));
            editMessageText.setReplyMarkup(null);
        } else if (callback.equals(messages.get("cart.delete"))) {
            CART_SERVICE.deletedCart(cartId);
            cartId = UUID.randomUUID();
            cartIdByUserId.put(userId, cartId);
            saveToFile();

            editMessageText.setText(messages.get("cart.cleared"));
            editMessageText.setReplyMarkup(null);
        } else if (callback.equals("Back")) {
            editMessageText.setText(messages.get("cart.menu"));
            editMessageText.setReplyMarkup(null);
        }
        return editMessageText;
    }
}
