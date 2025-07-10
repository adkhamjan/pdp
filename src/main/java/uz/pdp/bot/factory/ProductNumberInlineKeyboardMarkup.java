package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductNumberInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<String> {
    private final UUID productId;

    public ProductNumberInlineKeyboardMarkup(List<String> records, int colCount, UUID productId) {
        super(records, colCount);
        this.productId = productId;
    }

    public InlineKeyboardMarkup createInlineKeyboard(String back) {
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        i.setKeyboard(rows);

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(records.getFirst());
        btn1.setCallbackData("NUM:" + records.getFirst()+ ":" + records.get(1) +":"+productId);

        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText(records.get(1));
        btn2.setCallbackData("NUM:" + records.get(1)+ ":" + records.get(1) +":"+productId);

        InlineKeyboardButton btn3 = new InlineKeyboardButton();
        btn3.setText(records.get(2));
        btn3.setCallbackData("NUM:" + records.get(2)+ ":" + records.get(1) +":"+productId);
        row1.add(btn1);
        row1.add(btn2);
        row1.add(btn3);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton btn4 = new InlineKeyboardButton();
        btn4.setText(records.getLast());
        btn4.setCallbackData("NUM:" + records.getLast()+ ":" + records.get(1) +":"+productId);
        row2.add(btn4);

        rows.add(row1);
        rows.add(row2);
        rows.add(createBackInlineKeyboard(back));
        return i;
    }

    @Override
    protected List<InlineKeyboardButton> createBackInlineKeyboard(String back) {
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(back);
        btn.setCallbackData("NUM:Back:" + productId);
        rowBack.add(btn);
        return rowBack;
    }

    @Override
    protected RecordWrapper wrapper(String s) {
        return RecordWrapper.builder()
                .name(s)
                .id(productId)
                .command("NUM:")
                .build();
    }
}
