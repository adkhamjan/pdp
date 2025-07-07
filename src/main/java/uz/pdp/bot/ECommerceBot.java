package uz.pdp.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.factory.ProductNumberInlineKeyboardMarkup;
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.bot.service.CategoryBotService;
import uz.pdp.bot.service.LanguageBotService;
import uz.pdp.bot.service.ProductBotService;
import uz.pdp.enums.UserType;
import uz.pdp.model.CartItem;
import uz.pdp.model.Category;
import uz.pdp.model.Product;
import uz.pdp.model.User;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;
import uz.pdp.util.FileUtil;

import java.io.IOException;
import java.util.*;


@RequiredArgsConstructor
public class ECommerceBot extends TelegramLongPollingBot {
    private static final String USERNAME = "https://t.me/e_commerce_pro_Bot";
    private static final String BOT_TOKEN = "7766344482:AAEXYYB4phX2XvUnYZNhzVfO92dgormEg6c";
    private final CategoryService CATEGORY_SERVICE;
    private final ProductService PRODUCT_SERVICE;
    private final CartService CART_SERVICE;
    private final UserService USER_SERVICE;
    private static List<String> menuNames = LanguageBotService.getMenuNames();
    private static List<String> settingsName = LanguageBotService.getSettingNames();
    private static final String[] inlineProducts = {"-", "1", "+", "Savatga saqlash"};
    private static Map<Long, BotState> userStates = new HashMap<>();
    private static final String userIdsFile;
    private static final String cartIdByUserIdFile;

    private static Map<Long, UUID> userIds;
    private static Map<UUID, UUID> cartIdByUserId;

    static {
        userIdsFile = "userIds.json";
        cartIdByUserIdFile = "cartIdsByUserId.json";
        try {
            userIds = FileUtil.readMap(userIdsFile, Long.class);
            cartIdByUserId = FileUtil.readMap(cartIdByUserIdFile, UUID.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile() throws IOException {
        FileUtil.write(userIdsFile, userIds);
        FileUtil.write(cartIdByUserIdFile, cartIdByUserId);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();
            Long telUserId = message.getFrom().getId();
            UUID userId = userIds.get(telUserId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if (message.hasContact()) {
                Contact contact = message.getContact();
                String phoneNumber = contact.getPhoneNumber();

                sendMessage.setText("✅ Raqamingiz qabul qilindi: " + phoneNumber + "\nBotdan foydalanishingiz mumkin.");
                sendMessage.setText(menuNames.getFirst());
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));

                String firstName = message.getFrom().getFirstName();
                String username = message.getFrom().getUserName();
                User user = new User(firstName, username, phoneNumber, UserType.USER);
                USER_SERVICE.add(user);

                userId = user.getId();
                userIds.put(telUserId, userId);
                cartIdByUserId.put(userId, UUID.randomUUID());
                saveToFile();

            } else if (text.equals("/start")) {
                if (!userIds.containsKey(telUserId)) {
                    sendMessage.setText("Iltimos, botdan foydalanish uchun telefon raqamingizni yuboring:");
                    sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSendContactReplyKeyboardMarkup());
                } else {
                    sendMessage.setText(menuNames.getFirst());
                    sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
                }
            } else if (text.equals(menuNames.getFirst())) {
                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                sendMessage.setText(menuNames.getFirst());
                sendMessage.setReplyMarkup(categoryBotService.getInlineKeyboard());
            } else if (text.equals(menuNames.get(1))) {
                sendMessage.setText("bo'sh");
            } else if (text.equals(menuNames.get(2))) {
                sendMessage.setText("bo'sh");
            } else if (text.equals(menuNames.getLast())) {
                sendMessage.setText(menuNames.getLast());
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createSettingReplyKeyboardMarkup(settingsName));
            } else if (text.equals(settingsName.getFirst())) {
                sendMessage.setText("Tilni tanlang");
                sendMessage.setReplyMarkup(LanguageBotService.getInlineKeyboard());
            } else if (text.equals(settingsName.getLast())) {
                sendMessage.setText(menuNames.getFirst());
                sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
            }

                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            try {
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                Long telUserId = update.getCallbackQuery().getFrom().getId();
                UUID userId = userIds.get(telUserId);
                UUID cartId = cartIdByUserId.get(userId);

                String data = update.getCallbackQuery().getData();
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(messageId);


                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);

                if (data.startsWith("LANGUAGE:")) {
                    editMessageText.setText(menuNames.getLast());
                    editMessageText.setReplyMarkup(null);

                    String langId = data.split(":")[1];

                    if (langId.equals("6b8ca837-32a9-4cee-a218-cad716e7ca3d")) {
                        LanguageBotService.changeToUzb();
                        editMessageText.setText("Til muvaffaqiyatli o‘zgartirildi");
                    } else if (langId.equals("10853d6c-2ec3-4163-9a0c-bc5e48f76f75")) {
                        LanguageBotService.changeToEng();
                        editMessageText.setText("Language changed successfully");
                    } else if (langId.equals("3a65dfd5-db6c-45b1-b12a-60491e80fc08")) {
                        LanguageBotService.changeToRus();
                        editMessageText.setText("Язык успешно изменен");
                    }
                    if (!langId.equals("Back")) {
                        menuNames = LanguageBotService.getMenuNames();
                        settingsName = LanguageBotService.getSettingNames();
                        sendMessage.setText(menuNames.getFirst());
                        sendMessage.setReplyMarkup(ReplyKeyboardFactory.createReplyKeyboardMarkup(menuNames));
                    }
                    execute(sendMessage);
                } else if (data.startsWith("CATEGORY:")) {
                    String strId = data.split(":")[1];
                    if (strId.equals("Back")) {
                        UUID categoryId = UUID.fromString(data.split(":")[2]);
                        UUID parentId = CATEGORY_SERVICE.getParentIdByChildId(categoryId);
                        if (parentId == null) {
                            editMessageText.setText(menuNames.getFirst());
                            editMessageText.setReplyMarkup(null);
                        } else {
                            UUID currId = CATEGORY_SERVICE.getParentIdByChildId(parentId);
                            if (currId == null) {
                                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                                editMessageText.setText(menuNames.getFirst());
                                editMessageText.setReplyMarkup(categoryBotService.getInlineKeyboard());
                            } else {
                                Optional<Category> optionalCategory = CategoryService.getCategoryById(currId);
                                if (optionalCategory.isPresent()) {
                                    Category category = optionalCategory.get();
                                    CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                                    editMessageText.setText(category.getName());
                                    editMessageText.setReplyMarkup(categoryBotService.getInlineKeyboard(currId));
                                }
                            }
                        }
                    } else {
                        UUID catId = UUID.fromString(strId);
                        Optional<Category> optionalCategory = CategoryService.getCategoryById(catId);
                        if (optionalCategory.isPresent()) {
                            Category category = optionalCategory.get();
                            if (category.getNodeType()) {
                                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                                editMessageText.setText(category.getName());
                                editMessageText.setReplyMarkup(categoryBotService.getInlineKeyboard(catId));
                            }else {
                                ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE);
                                editMessageText.setText(category.getName());
                                editMessageText.setReplyMarkup(productBotService.getInlineKeyboard(catId));
                            }
                        }
                    }
                } else if (data.startsWith("PRODUCT:")) {
                    String strId = data.split(":")[1];
                    if (strId.equals("Back")) {
                        UUID productId = UUID.fromString(data.split(":")[2]);
                        Optional<Product> optionalProduct = ProductService.getProductById(productId);
                        if (optionalProduct.isPresent()) {
                            Product product = optionalProduct.get();
                            UUID parentCategoryId = CATEGORY_SERVICE.getParentIdByChildId(product.getCategoryId());
                            Optional<Category> optionalCategory = CategoryService.getCategoryById(parentCategoryId);
                            if (optionalCategory.isPresent()) {
                                Category category = optionalCategory.get();
                                CategoryBotService categoryBotService = new CategoryBotService(CATEGORY_SERVICE);
                                editMessageText.setText(category.getName());
                                editMessageText.setReplyMarkup(categoryBotService.getInlineKeyboard(parentCategoryId));
                            }
                        }
                    }else {
                        UUID productId = UUID.fromString(strId);
                        Optional<Product> optionalProduct = ProductService.getProductById(productId);
                        if (optionalProduct.isPresent()) {
                            Product product = optionalProduct.get();
                            editMessageText.setText("Nomi: " + product.getProductName() + "\n" + "Narxi: "+product.getPrice());
                            inlineProducts[1] = "1";
                            InlineKeyboardMarkup i = new ProductNumberInlineKeyboardMarkup(List.of(inlineProducts), 3, productId).createInlineKeyboard();
                            editMessageText.setReplyMarkup(i);
                        }
                    }
                } else if (data.startsWith("NUMBER")) {
                    String strId = data.split(":")[1];
                    if (strId.equals("Back")) {
                        UUID productId = UUID.fromString(data.split(":")[2]);
                        Optional<Product> optionalProduct = ProductService.getProductById(productId);
                        if (optionalProduct.isPresent()) {
                            Product product = optionalProduct.get();
                            Optional<Category> optionalCategory = CategoryService.getCategoryById(product.getCategoryId());
                            if (optionalCategory.isPresent()) {
                                Category category = optionalCategory.get();
                                ProductBotService productBotService = new ProductBotService(PRODUCT_SERVICE);
                                editMessageText.setText(category.getName());
                                editMessageText.setReplyMarkup(productBotService.getInlineKeyboard(category.getId()));
                            }
                        }
                    } else if (strId.equals(inlineProducts[0])) {
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
                    } else if (strId.equals(inlineProducts[2])) {
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
                    } else if (strId.equals(inlineProducts[3])){
                        UUID productId = UUID.fromString(data.split(":")[3]);
                        int quantity = Integer.parseInt(data.split(":")[2]);
                        CartItem cartItem = new CartItem(cartId, productId, quantity);
                        CART_SERVICE.addProductToCart(cartItem, userId);
                        cartIdByUserId.put(userId, UUID.randomUUID());
                        editMessageText.setText("Savatga qo'shildi");
                        editMessageText.setReplyMarkup(null);
                    }
                }
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }
}
