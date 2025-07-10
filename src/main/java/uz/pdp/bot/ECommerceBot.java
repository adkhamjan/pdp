package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.service.*;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;

import java.util.List;


public class ECommerceBot extends TelegramLongPollingBot {
    private static final String USERNAME = "e_commerce_pro_Bot";
    private static final String BOT_TOKEN = "7766344482:AAEXYYB4phX2XvUnYZNhzVfO92dgormEg6c";

    private final MessageHandlerService messageHandler;
    private final CallbackHandlerService callbackHandler;

    public ECommerceBot(CategoryService CATEGORY_SERVICE, ProductService PRODUCT_SERVICE, CartService CART_SERVICE, UserService USER_SERVICE) {
        this.messageHandler = new MessageHandlerService(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
        this.callbackHandler = new CallbackHandlerService(CATEGORY_SERVICE, PRODUCT_SERVICE, CART_SERVICE, USER_SERVICE);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            List<BotApiMethod<?>> botApiMethodList = messageHandler.handler(update, this);
            botApiMethodList.forEach(botApiMethod -> {
                try {
                    execute(botApiMethod);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (update.hasCallbackQuery()) {
            List<BotApiMethod<?>> botApiMethodList = callbackHandler.handler(update, this);
            botApiMethodList.forEach(botApiMethod -> {
                try {
                    execute(botApiMethod);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
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
