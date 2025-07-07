package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.ProductInlineKeyboardMarkup;
import uz.pdp.bot.factory.ProductNumberInlineKeyboardMarkup;
import uz.pdp.model.Product;
import uz.pdp.service.ProductService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductBotService {
    private final ProductService productService;

    public InlineKeyboardMarkup getInlineKeyboard(UUID categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return new ProductInlineKeyboardMarkup(products, 3)
                .createInlineKeyboard();
    }
}
