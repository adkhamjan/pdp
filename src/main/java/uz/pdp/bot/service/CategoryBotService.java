package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.factory.CategoryInlineKeyboardMarkup;
import uz.pdp.model.Category;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CategoryBotService {
    private final CategoryService categoryService;
    private final ProductService productService;

    public InlineKeyboardMarkup getInlineKeyboard(String back) {
        List<Category> categories = categoryService.getParentCategories();
        return new CategoryInlineKeyboardMarkup(categories, 3)
                .createInlineKeyboard(back);
    }

    public InlineKeyboardMarkup getInlineKeyboard(UUID id, String back) {
        List<Category> categories = categoryService.getChildCategoryById(id);
        return new CategoryInlineKeyboardMarkup(categories, 3)
                .createInlineKeyboard(back);
    }

    public EditMessageText getEditMessageByCategory(String data, EditMessageText editMessageText, Map<String, String> messages) {
        String strId = data.split(":")[1];
        if (strId.equals("Back")) {
            UUID categoryId = UUID.fromString(data.split(":")[2]);
            UUID parentId = categoryService.getParentIdByChildId(categoryId);
            if (parentId == null) {
                editMessageText.setText(messages.get("menu"));
                editMessageText.setReplyMarkup(null);
            } else {
                UUID currId = categoryService.getParentIdByChildId(parentId);
                if (currId == null) {
                    editMessageText.setText(messages.get("main") + messages.get("category.select"));
                    editMessageText.setReplyMarkup(getInlineKeyboard(messages.get("back")));
                } else {
                    Optional<Category> optionalCategory = CategoryService.getCategoryById(currId);
                    if (optionalCategory.isPresent()) {
                        Category category = optionalCategory.get();
                        editMessageText.setText(category.getName() + messages.get("category.select"));
                        editMessageText.setReplyMarkup(getInlineKeyboard(currId, messages.get("back")));
                    }
                }
            }
        } else {
            UUID catId = UUID.fromString(strId);
            Optional<Category> optionalCategory = CategoryService.getCategoryById(catId);
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                if (category.getNodeType()) {
                    editMessageText.setText(category.getName() + messages.get("category.select"));
                    editMessageText.setReplyMarkup(getInlineKeyboard(catId, messages.get("back")));
                } else {
                    ProductBotService productBotService = new ProductBotService(productService, categoryService);
                    editMessageText.setText(category.getName() + messages.get("product.select"));
                    editMessageText.setReplyMarkup(productBotService.getInlineKeyboard(catId, messages.get("back")));
                }
            }
        }
        return editMessageText;
    }

    public EditMessageText getEditMessageByParentCategory(EditMessageText editMessageText, UUID categoryId, Map<String, String> messages) {
        UUID parentCategoryId = categoryService.getParentIdByChildId(categoryId);
        Optional<Category> optionalCategory = CategoryService.getCategoryById(parentCategoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            editMessageText.setText(category.getName() + messages.get("category.select"));
            editMessageText.setReplyMarkup(getInlineKeyboard(parentCategoryId, messages.get("back")));
        }
        return editMessageText;
    }
}
