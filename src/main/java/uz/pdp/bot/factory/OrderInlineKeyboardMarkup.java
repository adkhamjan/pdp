package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;

import java.util.ArrayList;
import java.util.List;

public class OrderInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<String> {

    public OrderInlineKeyboardMarkup(List<String> records, int colCount) {
        super(records, colCount);
    }

    public InlineKeyboardMarkup createInlineKeyboard() {
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        i.setKeyboard(rows);

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(records.getFirst());
        btn1.setCallbackData("ORDER:" + records.getFirst());

        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText(records.get(1));
        btn2.setCallbackData("ORDER:" + records.getLast());
        row1.add(btn1);
        row1.add(btn2);

        rows.add(row1);
        rows.add(createBackInlineKeyboard());
        return i;
    }

    @Override
    protected List<InlineKeyboardButton> createBackInlineKeyboard() {
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("Back");
        btn.setCallbackData("ORDER:Back:");
        rowBack.add(btn);
        return rowBack;
    }

    @Override
    protected RecordWrapper wrapper(String s) {
        return RecordWrapper.builder()
                .name(s)
                .command("ORDER:")
                .build();
    }
}
