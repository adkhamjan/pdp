package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplyKeyboardFactory {

    public static ReplyKeyboardMarkup createMenuReplyKeyboardMarkup(List<String> buttons) {
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        r.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        r.setKeyboard(rows);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(buttons.getFirst()));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(buttons.get(1)));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(buttons.get(2)));
        row3.add(new KeyboardButton(buttons.getLast()));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return r;
    }

    public static ReplyKeyboardMarkup createReplyKeyboardMarkup(List<String> buttons, int colCount) {
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        r.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        r.setKeyboard(rows);

        int index = 0;
        KeyboardRow row = new KeyboardRow();
        for (String button: buttons) {
            index ++;
            row.add(new KeyboardButton(button));
            if (index % colCount == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }

        if (!row.isEmpty()) {
            rows.add(row);
        }
        return r;
    }

    public static ReplyKeyboardMarkup createSendContactReplyKeyboardMarkup() {
        KeyboardButton contactButton = new KeyboardButton();
        contactButton.setText("📱 Raqamni yuborish");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(Collections.singletonList(row));
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        return keyboardMarkup;
    }
}
