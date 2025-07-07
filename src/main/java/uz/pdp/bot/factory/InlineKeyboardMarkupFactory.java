package uz.pdp.bot.factory;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class InlineKeyboardMarkupFactory<T> {
    protected final List<T> records;
    private final int colCount;

    public InlineKeyboardMarkup createInlineKeyboard() {
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        i.setKeyboard(rows);

        int index = 0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (T record : records) {
            index ++;
            RecordWrapper wrapper = wrapper(record);

            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(wrapper.getName());
            btn.setCallbackData(wrapper.getCommand() + wrapper.getId());
            row.add(btn);

            if (index % colCount == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            rows.add(row);
        }

        rows.add(createBackInlineKeyboard());
        return i;
    }

    protected abstract List<InlineKeyboardButton> createBackInlineKeyboard();

    protected abstract RecordWrapper wrapper(T t);
}
