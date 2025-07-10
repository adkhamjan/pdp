package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguageInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Language>{

    public LanguageInlineKeyboardMarkup(List<Language> records, int colCount) {
        super(records, colCount);
    }

    @Override
    protected List<InlineKeyboardButton> createBackInlineKeyboard(String back) {
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(back);
        btn.setCallbackData("LANGUAGE:Back");
        rowBack.add(btn);
        return rowBack;
    }

    @Override
    protected RecordWrapper wrapper(Language language) {
        return RecordWrapper.builder()
                .id(language.getId())
                .name(language.getName())
                .command("LANGUAGE:")
                .build();
    }
}
