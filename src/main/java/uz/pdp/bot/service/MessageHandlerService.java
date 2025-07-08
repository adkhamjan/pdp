package uz.pdp.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import uz.pdp.bot.ECommerceBot;

import uz.pdp.bot.factory.OrderInlineKeyboardMarkup;
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.enums.UserType;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;

import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MessageHandlerService extends BotHandlerService {

    public MessageHandlerService(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        super(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @SneakyThrows
    @Override

    public BotApiMethod<?> handler(Update update, ECommerceBot eCommerceBot) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();
            Long telUserId = message.getFrom().getId();
            UUID userId = userIds.get(telUserId);
            UUID cartId = cartIdByUserId.get(userId);

            String lang = languages.getOrDefault(userId, "uz");
            Map<String, String> messages = LanguageBotService.getTexts(lang);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            List<String> menuName = List.of(messages.get("menu.menu"), messages.get("menu.orders"),
                    messages.get("menu.cart"), messages.get("menu.settings"));

            if (message.hasContact()) {
                return registerContact(message, sendMessage, menuName);
            } else if (text.equals("/start")) {
                if (!userIds.containsKey(telUserId)) {
                    sendMessage.setText(messages.get("menu.contact"));
                    sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSendContactReplyKeyboardMarkup());
                } else {
                    sendMessage.setText(messages.get("menu.menu"));
                    sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuName));
                }
            } else if (text.equals(messages.get("menu.menu"))) {
                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE, PRODUCT_SERVICE);
                sendMessage.setText(messages.get("menu.menu"));
                sendMessage.setReplyMarkup(categoryBotService.getInlineKeyboard());
            } else if (text.equals(messages.get("menu.orders"))) {
                return getSendMessageOrders(sendMessage, userId, messages);
            } else if (text.equals(messages.get("menu.cart"))) {
                return getSendMessageCartById(sendMessage, cartId, messages);
            } else if (text.equals(messages.get("menu.settings"))) {
                sendMessage.setText(messages.get("menu.settings"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSettingReplyKeyboardMarkup(List.of(messages.get("language.change"), messages.get("back"))));
            } else if (text.equals(messages.get("language.change"))) {
                sendMessage.setText(messages.get("language.prompt"));
                sendMessage.setReplyMarkup(LanguageBotService.getInlineKeyboard());
            } else if (text.equals(messages.get("back"))) {
                sendMessage.setText(messages.get("menu.menu"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuName));
            }
            return sendMessage;
        }
        return null;
    }

    @SneakyThrows
    private SendMessage registerContact(Message message, SendMessage sendMessage, List<String> menuNames) {
        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();

        sendMessage.setText("✅ Raqamingiz qabul qilindi: " + phoneNumber + "\nBotdan foydalanishingiz mumkin.");
        sendMessage.setText(menuNames.getFirst());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));

        String firstName = message.getFrom().getFirstName();
        String username = message.getFrom().getUserName();
        User user = new User(firstName, username, phoneNumber, UserType.USER);
        USER_SERVICE.add(user);

        Long telUserId = message.getFrom().getId();
        UUID userId = user.getId();
        userIds.put(telUserId, userId);
        cartIdByUserId.put(userId, UUID.randomUUID());
        languages.put(userId, "uz");
        saveToFile();

        return sendMessage;
    }

    private SendMessage getSendMessageOrders(SendMessage sendMessage, UUID userId, Map<String, String> messages) {
        List<Cart> orders = CART_SERVICE.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            sendMessage.setText(messages.get("order.none"));
        } else {
            StringBuilder sb = new StringBuilder();
            for (Cart order : orders) {
                List<CartItem> cartItems = order.getCartItemList();
                sb.append(messages.get("product.price")).append(order.getTotalPrice()).append("\n");
                for (CartItem cartItem : cartItems) {
                    Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
                    optionalProduct.ifPresent(product -> sb.append(product.getProductName()).append(": ").
                            append(cartItem.getQuantity()).append("\n"));
                }
                sb.append("=======================\n");
            }
            sendMessage.setText(sb.toString());
        }
        return sendMessage;
    }

    private SendMessage getSendMessageCartById(SendMessage sendMessage, UUID cartId, Map<String, String> messages) {
        Cart cart = CART_SERVICE.getCartByCartId(cartId);
        if (cart == null) {
            sendMessage.setText(messages.get("cart.empty"));
        } else {
            List<CartItem> cartItems = cart.getCartItemList();
            StringBuilder sb = new StringBuilder();
            sb.append(messages.get("product.price")).append(cart.getTotalPrice()).append("\n");
            for (CartItem cartItem : cartItems) {
                Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
                optionalProduct.ifPresent(product -> sb.append(product.getProductName()).append(": ").
                        append(cartItem.getQuantity()).append("\n"));
            }
            sendMessage.setText(sb.toString());
            String[] inlineOrders = {messages.get("cart.order"), messages.get("cart.delete")};
            InlineKeyboardMarkup i = new OrderInlineKeyboardMarkup(List.of(inlineOrders), 2)
                    .createInlineKeyboard();
            sendMessage.setReplyMarkup(i);
        }
        return sendMessage;
    }
}
