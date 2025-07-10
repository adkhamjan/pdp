package uz.pdp.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.factory.wrapper.RecordWrapper;
import uz.pdp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductInlineKeyboardMarkup extends InlineKeyboardMarkupFactory<Product> {

    public ProductInlineKeyboardMarkup(List<Product> records, int colCount) {
        super(records, colCount);
    }

    @Override
    protected List<InlineKeyboardButton> createBackInlineKeyboard(String back) {
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(back);
        btn.setCallbackData("PRODUCT:Back:"+records.getFirst().getId());
        rowBack.add(btn);
        return rowBack;
    }

    @Override
    protected RecordWrapper wrapper(Product product) {
        return RecordWrapper.builder()
                .id(product.getId())
                .name(product.getProductName())
                .command("PRODUCT:")
                .build();
    }
}
