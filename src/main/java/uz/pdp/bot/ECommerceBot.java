package uz.pdp.bot;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.bot.service.CategoryBotService;
import uz.pdp.bot.service.LanguageBotService;
import uz.pdp.service.CategoryService;

import java.util.List;


@RequiredArgsConstructor
public class ECommerceBot extends TelegramLongPollingBot {
    private static final String USERNAME = "https://t.me/e_commerce_pro_Bot";
    private static final String BOT_TOKEN = "7766344482:AAEXYYB4phX2XvUnYZNhzVfO92dgormEg6c";
    private final CategoryService CATEGORY_SERVICE;
    private static List<String> menuNames = LanguageBotService.getMenuNames();
    private static List<String> settingsName = LanguageBotService.getSettingNames();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Hi");
            if (text.equals("/start")) {
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
            } else if (text.equals(menuNames.getFirst())) {
                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                sendMessage.setReplyMarkup(categoryBotService.getInlineKeyboard());
            } else if (text.equals(menuNames.get(1))) {
                sendMessage.setText("bo'sh");
            } else if (text.equals(menuNames.get(2))) {
                sendMessage.setText("bo'sh");
            } else if (text.equals(menuNames.getLast())) {
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSettingReplyKeyboardMarkup(settingsName));
            } else if (text.equals(settingsName.getFirst())) {
                sendMessage.setReplyMarkup(LanguageBotService.getInlineKeyboard());
            } else if (text.equals(settingsName.getLast())) {
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
            }

            execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            try {
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                String data = update.getCallbackQuery().getData();

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);

                if (data.startsWith("LANGUAGE:")) {
                    String langId = data.split(":")[1];

                    if (langId.equals("6b8ca837-32a9-4cee-a218-cad716e7ca3d")) {
                        LanguageBotService.changeToUzb();
                        sendMessage.setText("Til muvaffaqiyatli o‘zgartirildi");
                    } else if (langId.equals("10853d6c-2ec3-4163-9a0c-bc5e48f76f75")) {
                        LanguageBotService.changeToEng();
                        sendMessage.setText("Language changed successfully");
                    } else {
                        LanguageBotService.changeToRus();
                        sendMessage.setText("Язык успешно изменен");
                    }
                    menuNames = LanguageBotService.getMenuNames();
                    settingsName = LanguageBotService.getSettingNames();
                    sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
                }

                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
