package uz.pdp.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.service.*;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;



public class ECommerceBot extends TelegramLongPollingBot {
    private static final String USERNAME = "https://t.me/e_commerce_pro_Bot";
    private static final String BOT_TOKEN = "7766344482:AAEXYYB4phX2XvUnYZNhzVfO92dgormEg6c";

    private final MessageHandlerService messageHandler;
    private final CallbackHandlerService callbackHandler;

    public ECommerceBot(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        this.messageHandler = new MessageHandlerService(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
        this.callbackHandler = new CallbackHandlerService(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            execute(messageHandler.handler(update));
        } else if (update.hasCallbackQuery()) {
            execute(callbackHandler.handler(update));
        }
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }
}
