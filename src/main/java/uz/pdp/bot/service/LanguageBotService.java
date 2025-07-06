package uz.pdp.bot.service;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.LanguageInlineKeyboardMarkup;
import uz.pdp.model.Language;
import uz.pdp.service.LanguageService;

import java.util.List;

public class LanguageBotService {
    @Getter
    private static List<String> menuNames;
    @Getter
    private static List<String> settingNames;

    private static final List<String> uzbName;
    private static final List<String> engName;
    private static final List<String> rusName;
    private static final List<String> uzbSettingName;
    private static final List<String> engSettingName;
    private static final List<String> rusSettingName;

    static {
        uzbName = List.of("Menyu", "Mening buyurtmalarim", "Savat", "Sozlamalar");
        engName = List.of("Menu", "My orders", "Cart", "Settings");
        rusName = List.of("Меню", "Мои заказы", "Корзина", "Настройки");
        uzbSettingName = List.of("Til o'zgartirish", "Ortga");
        engSettingName = List.of("Change language", "Back");
        rusSettingName = List.of("Изменить язык", "Назад");

        menuNames = uzbName;
        settingNames = uzbSettingName;
    }

    public static void changeToUzb() {
        menuNames = uzbName;
        settingNames = uzbSettingName;
    }

    public static void changeToEng() {
        menuNames = engName;
        settingNames = engSettingName;
    }

    public static void changeToRus() {
        menuNames = rusName;
        settingNames = rusSettingName;
    }

    public static InlineKeyboardMarkup getInlineKeyboard() {
        List<Language> languages = LanguageService.getLanguage();
        return new LanguageInlineKeyboardMarkup(languages, 3)
                .createInlineKeyboard();
    }
}
