package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.CategoryInlineKeyboardMarkup;
import uz.pdp.model.Category;
import uz.pdp.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CategoryBotService {
    private final CategoryService categoryService;

    public InlineKeyboardMarkup getInlineKeyboard() {
        List<Category> categories = categoryService.getParentCategories();
        return new CategoryInlineKeyboardMarkup(categories, 3)
                .createInlineKeyboard();
    }

    public InlineKeyboardMarkup getInlineKeyboard(UUID id) {
        List<Category> categories = categoryService.getChildCategoryById(id);
        return new CategoryInlineKeyboardMarkup(categories, 3)
                .createInlineKeyboard();
    }
}
