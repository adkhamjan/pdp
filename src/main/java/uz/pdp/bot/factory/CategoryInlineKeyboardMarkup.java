package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Category>{

    public CategoryInlineKeyboardMarkup(List<Category> records, int colCount) {
        super(records, colCount);
    }

    @Override
    protected List<InlineKeyboardButton> createBackInlineKeyboard(String back) {
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(back);
        btn.setCallbackData("CATEGORY:Back:" + records.getFirst().getId());
        rowBack.add(btn);
        return rowBack;
    }

    @Override
    protected RecordWrapper wrapper(Category category) {
        return RecordWrapper.builder()
                .id(category.getId())
                .name(category.getName())
                .command("CATEGORY:")
                .build();
    }
}
