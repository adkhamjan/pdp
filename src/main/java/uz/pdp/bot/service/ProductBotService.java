package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.ECommerceBot;

import uz.pdp.bot.factory.ProductInlineKeyboardMarkup;
import uz.pdp.bot.factory.ProductNumberInlineKeyboardMarkup;
import uz.pdp.model.CartItem;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductBotService {
    private final ProductService productService;
    private final CategoryService categoryService;

    public InlineKeyboardMarkup getInlineKeyboard(UUID categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return new ProductInlineKeyboardMarkup(products, 3)
                .createInlineKeyboard();
    }


    @SneakyThrows
    public EditMessageText getEditMessageByProduct(EditMessageText editMessageText, String data, String[] inlineProducts, ECommerceBot eCommerceBot) {

        String strId = data.split(":")[1];
        if (strId.equals("Back")) {
            UUID productId = UUID.fromString(data.split(":")[2]);
            Optional<Product> optionalProduct = ProductService.getProductById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                CategoryBotService categoryBotService = new CategoryBotService(categoryService, productService);
                return categoryBotService.getEditMessageByParentCategory(editMessageText, product.getCategoryId());
            }
        } else {
            UUID productId = UUID.fromString(strId);
            Optional<Product> optionalProduct = ProductService.getProductById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();

                inlineProducts[1] = "1";




                editMessageText.setText("Nomi: " + product.getProductName() + "\n" + "Narxi: " + product.getPrice());
                inlineProducts[1] = "1";
                InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
                editMessageText.setReplyMarkup(i);

                org.telegram.telegrambots.meta.api.methods.send.SendPhoto photo = new org.telegram.telegrambots.meta.api.methods.send.SendPhoto();
                photo.setChatId(String.valueOf(editMessageText.getChatId()));
                photo.setPhoto(new org.telegram.telegrambots.meta.api.objects.InputFile(product.getImageUrl()));
                eCommerceBot.execute(photo);

            }
        }
        return editMessageText;
    }

    public EditMessageText getEditMessageProductQuantity(EditMessageText editMessageText, String data, String[] inlineProducts, UUID cartId, UUID userId, CartService cartService) {
        String strId = data.split(":")[1];
        if (strId.equals("Back")) {
            return getEditMessageQuantityBack(editMessageText, data);
        } else if (strId.equals(inlineProducts[0])) {
            return getEditMessageAddProduct(editMessageText, data, inlineProducts);
        } else if (strId.equals(inlineProducts[2])) {
            return getEditMessageSubtractionProduct(editMessageText, data, inlineProducts);
        } else if (strId.equals(inlineProducts[3])) {
            UUID productId = UUID.fromString(data.split(":")[3]);
            int quantity = Integer.parseInt(data.split(":")[2]);
            CartItem cartItem = new CartItem(cartId, productId, quantity);
            cartService.addProductToCart(cartItem, userId);

            editMessageText.setText("Savatga qo'shildi");
            editMessageText.setReplyMarkup(null);
        }
        return editMessageText;
    }

    private EditMessageText getEditMessageQuantityBack(EditMessageText editMessageText, String data) {
        UUID productId = UUID.fromString(data.split(":")[2]);
        Optional<Product> optionalProduct = ProductService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Optional<Category> optionalCategory = CategoryService.getCategoryById(product.getCategoryId());
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                editMessageText.setText(category.getName());
                editMessageText.setReplyMarkup(getInlineKeyboard(category.getId()));
            }
        }
        return editMessageText;
    }

    private EditMessageText getEditMessageAddProduct(EditMessageText editMessageText, String data, String[] inlineProducts) {
        int count = Integer.parseInt(data.split(":")[2]);
        if (count > 1) {
            count -= 1;
            inlineProducts[1] = String.valueOf(count);
            UUID productId = UUID.fromString(data.split(":")[3]);
            Optional<Product> optionalProduct = ProductService.getProductById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                editMessageText.setText("Nomi: " + product.getProductName() + "\n" + "Narxi: " + product.getPrice());
                InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
                editMessageText.setReplyMarkup(i);
            }
        }
        return editMessageText;
    }

    private EditMessageText getEditMessageSubtractionProduct(EditMessageText editMessageText, String data, String[] inlineProducts) {
        int count = Integer.parseInt(data.split(":")[2]) + 1;
        inlineProducts[1] = String.valueOf(count);
        UUID productId = UUID.fromString(data.split(":")[3]);
        Optional<Product> optionalProduct = ProductService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            editMessageText.setText("Nomi: " + product.getProductName() + "\n" + "Narxi: " + product.getPrice());
            InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
            editMessageText.setReplyMarkup(i);
        }
        return editMessageText;
    }

}
