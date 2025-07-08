package uz.pdp.bot.service;

import lombok.Data;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.LanguageInlineKeyboardMarkup;
import uz.pdp.model.Language;
import uz.pdp.service.LanguageService;
import uz.pdp.util.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LanguageBotService {
    private static Map<String, String> uz_messages;
    private static Map<String, String> eng_messages;
    private static Map<String, String> rus_messages;

    static {
        try {
            uz_messages = FileUtil.readMap("./messages/messages_uz.json", String.class, String.class);
            eng_messages = FileUtil.readMap("./messages/messages_eng.json", String.class, String.class);
            rus_messages = FileUtil.readMap("./messages/messages_ru.json", String.class, String.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InlineKeyboardMarkup getInlineKeyboard() {
        List<Language> languages = LanguageService.getLanguage();
        return new LanguageInlineKeyboardMarkup(languages, 3)
                .createInlineKeyboard();
    }

    public static Map<String, String> getTexts(String lang) {
        if (lang.equals("uz")) return uz_messages;
        if (lang.equals("eng")) return eng_messages;
        return rus_messages;
    }

    public static EditMessageText getEditMessageChangeLang(EditMessageText editMessageText, String data, Map<String, String> messages, Map<UUID, String> languages, UUID userId) {
        editMessageText.setText(messages.get("menu.settings"));
        editMessageText.setReplyMarkup(null);

        String langId = data.split(":")[1];

        switch (langId) {
            case "6b8ca837-32a9-4cee-a218-cad716e7ca3d" -> {
                languages.put(userId, "uz");
                editMessageText.setText("Til muvaffaqiyatli o‘zgartirildi");
            }
            case "10853d6c-2ec3-4163-9a0c-bc5e48f76f75" -> {
                languages.put(userId, "eng");
                editMessageText.setText("Language changed successfully");
            }
            case "3a65dfd5-db6c-45b1-b12a-60491e80fc08" -> {
                languages.put(userId, "rus");
                editMessageText.setText("Язык успешно изменен");
            }
        }
        return editMessageText;
    }
}
