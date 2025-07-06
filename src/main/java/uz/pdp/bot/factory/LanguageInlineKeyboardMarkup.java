package uz.pdp.bot.factory;

import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Language;

import java.util.List;

public class LanguageInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Language>{

    public LanguageInlineKeyboardMarkup(List<Language> records, int colCount) {
        super(records, colCount);
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
