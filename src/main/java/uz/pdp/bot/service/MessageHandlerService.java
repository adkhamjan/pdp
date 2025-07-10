package uz.pdp.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.ECommerceBot;
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.enums.BotState;
import uz.pdp.enums.UserType;
import uz.pdp.model.Cart;
import uz.pdp.model.CartItem;

import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.*;

public class MessageHandlerService extends BotHandlerService {

    public MessageHandlerService(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        super(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @SneakyThrows
    @Override
    public List<BotApiMethod<?>> handler(Update update, ECommerceBot eCommerceBot) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        Long tmeUserId = message.getFrom().getId();
        UUID userId = userIds.get(tmeUserId);
        UUID cartId = cartIdByUserId.get(userId);

        String lang = languages.getOrDefault(userId, "uz");
        Map<String, String> messages = LanguageBotService.getTexts(lang);

        BotState userState = userStates.get(tmeUserId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        List<String> menuName = List.of(messages.get("menu.menu"), messages.get("menu.orders"),
                messages.get("menu.cart"), messages.get("menu.settings"));

        if (message.hasContact() && userState.equals(BotState.WAITING_CONTACT)) {
            result.add(registerContact(message, sendMessage, menuName));
        } else if (text.equals("/start")) {
            result.add(sendMessage);
            if (!userIds.containsKey(tmeUserId)) {
                sendMessage.setText(messages.get("menu.contact"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSendContactReplyKeyboardMarkup());
                userStates.put(chatId, BotState.WAITING_CONTACT);
            } else {
                sendMessage.setText(messages.get("menu.menu"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuName));
                userStates.put(tmeUserId, BotState.MAIN_MENU);
            }
            saveStateToFile();
        } else if (!userState.equals(BotState.WAITING_CONTACT)) {
            if (text.equals(messages.get("menu.menu"))) {
                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE, PRODUCT_SERVICE);
                sendMessage.setText("Bosh kategoriyalardan birini tanlang");
                sendMessage.setReplyMarkup(categoryBotService.getInlineKeyboard());
                result.add(sendMessage);
            } else if (text.equals(messages.get("menu.orders"))) {
                return getSendMessageOrders(chatId, userId, messages);
            } else if (text.equals(messages.get("menu.cart"))) {
                result.add(getSendMessageCartById(sendMessage, cartId, messages, tmeUserId));
            } else if (text.equals(messages.get("menu.settings"))) {
                sendMessage.setText(messages.get("menu.settings"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(List.of(messages.get("language.change"),
                        messages.get("back")), 1));
                result.add(sendMessage);
                userStates.put(tmeUserId, BotState.VIEWING_SETTING);
                saveStateToFile();
            } else if (text.equals(messages.get("language.change"))) {
                sendMessage.setText(messages.get("language.prompt"));
                sendMessage.setReplyMarkup(LanguageBotService.getInlineKeyboard());
                result.add(sendMessage);
            } else if (text.equals(messages.get("back"))) {
                sendMessage.setText(messages.get("menu.menu"));
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuName));
                userStates.put(tmeUserId, BotState.MAIN_MENU);
                saveStateToFile();
                result.add(sendMessage);
            } else {
                sendMessage = getSendMessageByOrder(sendMessage, text, userId, tmeUserId, messages, menuName);
                if (sendMessage != null) result.add(sendMessage);
            }
        }
        return result;
    }

    @SneakyThrows
    private SendMessage registerContact(Message message, SendMessage sendMessage, List<String> menuNames) {
        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();

        sendMessage.setText("✅ Raqamingiz qabul qilindi: " + phoneNumber + "\nBotdan foydalanishingiz mumkin.");
        sendMessage.setText(menuNames.getFirst());
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuNames));

        String firstName = message.getFrom().getFirstName();
        String username = message.getFrom().getUserName();
        User user = new User(firstName, username, phoneNumber, UserType.USER);
        USER_SERVICE.add(user);

        Long tmeUserId = message.getFrom().getId();
        UUID userId = user.getId();
        userIds.put(tmeUserId, userId);
        cartIdByUserId.put(userId, UUID.randomUUID());
        languages.put(userId, "uz");
        userStates.put(tmeUserId, BotState.MAIN_MENU);
        saveToFile();

        return sendMessage;
    }

    private List<BotApiMethod<?>> getSendMessageOrders(Long chatId, UUID userId, Map<String, String> messages) {
        List<Cart> orders = CART_SERVICE.getOrdersByUserId(userId);
        List<BotApiMethod<?>> result = new ArrayList<>();
        if (orders.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(messages.get("order.none"));
            result.add(sendMessage);
        } else {
            for (Cart order : orders) {
                StringBuilder sb = new StringBuilder();
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);

                List<CartItem> cartItems = order.getCartItemList();
                sb.append(messages.get("product.price")).append(order.getTotalPrice()).append("\n");
                for (CartItem cartItem : cartItems) {
                    Optional<Product> optionalProduct = ProductService.getProductById(cartItem.getProductId());
                    optionalProduct.ifPresent(product -> sb.append(product.getProductName()).append(": ").
                            append(cartItem.getQuantity()).append("\n"));
                }
                sendMessage.setText(sb.toString());
                result.add(sendMessage);
            }
        }
        return result;
    }

    @SneakyThrows
    private SendMessage getSendMessageCartById(SendMessage sendMessage, UUID cartId, Map<String, String> messages, Long tmeUserId) {
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
            sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(List.of(messages.get("cart.order"),
                    messages.get("cart.delete"), messages.get("back")), 2));
            userStates.put(tmeUserId, BotState.VIEWING_CART);
            saveStateToFile();
        }
        return sendMessage;
    }

    @SneakyThrows
    private SendMessage getSendMessageByOrder(SendMessage sendMessage, String text, UUID userId, Long tmeUserId, Map<String, String> messages, List<String> menuNames) {
        UUID cartId = cartIdByUserId.get(userId);
        if (text.equals(messages.get("cart.order"))) {
            Cart cart = CART_SERVICE.getCartByCartId(cartId);
            CART_SERVICE.addCartToOrders(cart);
            cartId = UUID.randomUUID();
            cartIdByUserId.put(userId, cartId);
            saveCartIdToFile();

            sendMessage.setText(messages.get("order.placed"));
            sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuNames));
            return sendMessage;
        } else if (text.equals(messages.get("cart.delete"))) {
            CART_SERVICE.deletedCart(cartId);
            cartId = UUID.randomUUID();
            cartIdByUserId.put(userId, cartId);
            saveCartIdToFile();

            sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuNames));
            sendMessage.setText(messages.get("cart.cleared"));
            return sendMessage;
        } else if (text.equals(messages.get("back"))) {
            sendMessage.setText(messages.get("cart.menu"));
            sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuNames));
            userStates.put(tmeUserId, BotState.MAIN_MENU);
            saveStateToFile();
            return sendMessage;
        }
        return null;
    }
}
