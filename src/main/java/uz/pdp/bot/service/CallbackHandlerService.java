package uz.pdp.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import uz.pdp.bot.ECommerceBot;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CallbackHandlerService extends BotHandlerService {
    public CallbackHandlerService(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        super(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @SneakyThrows
    @Override
    public List<BotApiMethod<?>> handler(Update update, ECommerceBot eCommerceBot) {
        List<BotApiMethod<?>> result = new ArrayList<>();
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

        String[] inlineProducts = {"➖", "1", "➕", messages.get("cart.save")};

        if (data.startsWith("LANGUAGE:")) {
            result = LanguageBotService.getEditMessageChangeLang(editMessageText, data, messages, languages, userId);
            saveLanguageToFile();
        } else if (data.startsWith("CATEGORY:")) {
            CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE, PRODUCT_SERVICE);
            result.add(categoryBotService.getEditMessageByCategory(data, editMessageText, messages));
        } else if (data.startsWith("PRODUCT:")) {
            ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE, CATEGORY_SERVICE);
            result.add(productBotService.getBotApiMethodByProduct(editMessageText, data, inlineProducts, eCommerceBot, messages));
        } else if (data.startsWith("NUM")) {
            ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE, CATEGORY_SERVICE);
            result = productBotService.getEditMessageProductQuantity(editMessageText, data, inlineProducts, cartId, userId, CART_SERVICE, messages);
        }
        return result;
    }
}
