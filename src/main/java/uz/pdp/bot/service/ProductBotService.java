package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

import java.util.ArrayList;
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
    public BotApiMethod<?> getBotApiMethodByProduct(EditMessageText editMessageText, String data, String[] inlineProducts, ECommerceBot eCommerceBot) {
        String strId = data.split(":")[1];
        if (strId.equals("Back")) {
            UUID productId = UUID.fromString(data.split(":")[2]);
            Optional<Product> optionalProduct = ProductService.getProductById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                CategoryBotService categoryBotService = new CategoryBotService(categoryService, productService);
                return categoryBotService.getEditMessageByParentCategory(editMessageText, product.getCategoryId());
            }
        }
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(editMessageText.getChatId());
        deleteMessage.setMessageId(editMessageText.getMessageId());

        org.telegram.telegrambots.meta.api.methods.send.SendPhoto sendPhoto = new org.telegram.telegrambots.meta.api.methods.send.SendPhoto();

        UUID productId = UUID.fromString(strId);
        Optional<Product> optionalProduct = ProductService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            inlineProducts[1] = "1";
            InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();

            sendPhoto.setChatId(editMessageText.getChatId());
            sendPhoto.setPhoto(new org.telegram.telegrambots.meta.api.objects.InputFile(product.getImageUrl()));
            sendPhoto.setCaption("Nomi: " + product.getProductName() + "\n" + "Narxi: " + product.getPrice());
            sendPhoto.setReplyMarkup(i);
            eCommerceBot.execute(sendPhoto);
        }
        return deleteMessage;
    }

    public List<BotApiMethod<?>> getEditMessageProductQuantity(EditMessageText editMessageText, String data, String[] inlineProducts, UUID cartId, UUID userId, CartService cartService) {
        List<BotApiMethod<?>> result = new ArrayList<>();

        String strId = data.split(":")[1];
        if (strId.equals("Back")) {
            return getEditMessageQuantityBack(editMessageText, data);
        } else if (strId.equals(inlineProducts[2])) {
            result.add(getEditMessageReplyMarkupAddProduct(editMessageText, data, inlineProducts));
        } else if (strId.equals(inlineProducts[0])) {
            EditMessageReplyMarkup subtractionProduct = subtractionProduct(editMessageText, data, inlineProducts);
            if (subtractionProduct != null) {
                result.add(subtractionProduct);
            }
        } else if (strId.equals(inlineProducts[3])) {
            UUID productId = UUID.fromString(data.split(":")[3]);
            int quantity = Integer.parseInt(data.split(":")[2]);
            CartItem cartItem = new CartItem(cartId, productId, quantity);
            cartService.addProductToCart(cartItem, userId);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(editMessageText.getChatId());
            deleteMessage.setMessageId(editMessageText.getMessageId());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(editMessageText.getChatId());
            sendMessage.setText("Savatga qo'shildi");
            result.add(deleteMessage);
            result.add(sendMessage);
        }
        return result;
    }

    private List<BotApiMethod<?>> getEditMessageQuantityBack(EditMessageText editMessageText, String data) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(editMessageText.getChatId());
        deleteMessage.setMessageId(editMessageText.getMessageId());
        List<BotApiMethod<?>> result = new ArrayList<>();
        result.add(deleteMessage);

        UUID productId = UUID.fromString(data.split(":")[2]);
        Optional<Product> optionalProduct = ProductService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Optional<Category> optionalCategory = CategoryService.getCategoryById(product.getCategoryId());
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(editMessageText.getChatId());
                sendMessage.setText(category.getName());
                sendMessage.setReplyMarkup(getInlineKeyboard(category.getId()));
                result.add(sendMessage);
            }
        }
        return result;
    }

    private EditMessageReplyMarkup subtractionProduct(EditMessageText editMessageText, String data, String[] inlineProducts) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(editMessageText.getMessageId());
        editMessageReplyMarkup.setChatId(editMessageText.getChatId());

        int count = Integer.parseInt(data.split(":")[2]);
        if (count > 1) {
            count -= 1;
            inlineProducts[1] = String.valueOf(count);
            UUID productId = UUID.fromString(data.split(":")[3]);
            InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
            editMessageReplyMarkup.setReplyMarkup(i);
            return editMessageReplyMarkup;
        }
        return null;
    }

    private EditMessageReplyMarkup getEditMessageReplyMarkupAddProduct(EditMessageText editMessageText, String data, String[] inlineProducts) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(editMessageText.getMessageId());
        editMessageReplyMarkup.setChatId(editMessageText.getChatId());

        int count = Integer.parseInt(data.split(":")[2]) + 1;
        inlineProducts[1] = String.valueOf(count);
        UUID productId = UUID.fromString(data.split(":")[3]);
        InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
        editMessageReplyMarkup.setReplyMarkup(i);
        return editMessageReplyMarkup;
    }

}
