package uz.pdp.bot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.LanguageInlineKeyboardMarkup;
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.model.Language;
import uz.pdp.service.LanguageService;
import uz.pdp.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LanguageBotService {
    private static final Map<String, String> uz_messages;
    private static final Map<String, String> eng_messages;
    private static final Map<String, String> rus_messages;

    static {
        try {
            uz_messages = FileUtil.readMap("./messages/messages_uz.json", String.class, String.class);
            eng_messages = FileUtil.readMap("./messages/messages_eng.json", String.class, String.class);
            rus_messages = FileUtil.readMap("./messages/messages_ru.json", String.class, String.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InlineKeyboardMarkup getInlineKeyboard(String back) {
        List<Language> languages = LanguageService.getLanguage();
        return new LanguageInlineKeyboardMarkup(languages, 3)
                .createInlineKeyboard(back);
    }

    public static Map<String, String> getTexts(String lang) {
        if (lang.equals("uz")) return uz_messages;
        if (lang.equals("eng")) return eng_messages;
        return rus_messages;
    }

    public static List<BotApiMethod<?>> getEditMessageChangeLang(EditMessageText editMessageText, String data, Map<String, String> messages, Map<UUID, String> languages, UUID userId) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        String langId = data.split(":")[1];

        if (langId.equals("Back")) {
            editMessageText.setText(messages.get("menu.settings"));
            editMessageText.setReplyMarkup(null);
            result.add(editMessageText);
            return result;
        }
        switch (langId) {
            case "6b8ca837-32a9-4cee-a218-cad716e7ca3d" -> {
                languages.put(userId, "uz");
                messages = uz_messages;
            }
            case "10853d6c-2ec3-4163-9a0c-bc5e48f76f75" -> {
                languages.put(userId, "eng");
                messages = eng_messages;
            }
            case "3a65dfd5-db6c-45b1-b12a-60491e80fc08" -> {
                languages.put(userId, "rus");
                messages = rus_messages;
            }
        }
        editMessageText.setText(messages.get("language.changed"));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(editMessageText.getChatId());

        sendMessage.setText(messages.get("main"));
        List<String> menuName = List.of(messages.get("menu.menu"), messages.get("menu.orders"),
                messages.get("menu.cart"), messages.get("menu.settings"));
        sendMessage.setReplyMarkup(ReplyKeyboardFactory.createMenuReplyKeyboardMarkup(menuName));
        result.add(editMessageText);
        result.add(sendMessage);
        return result;
    }
}
