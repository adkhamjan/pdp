package uz.pdp.bot.service;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.ECommerceBot;
import uz.pdp.service.CartService;
import uz.pdp.service.CategoryService;
import uz.pdp.service.ProductService;
import uz.pdp.service.UserService;
import uz.pdp.util.FileUtil;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BotHandlerService {
    protected static final String userIdsFile;
    protected static final String cartIdByUserIdFile;
    protected static final String languageFile;

    protected static final Map<Long, UUID> userIds;
    protected static final Map<UUID, UUID> cartIdByUserId;
    protected static Map<UUID, String> languages;

    protected final CategoryService CATEGORY_SERVICE;
    protected final ProductService PRODUCT_SERVICE;
    protected final CartService CART_SERVICE;
    protected final UserService USER_SERVICE;

    static {
        languageFile = "user_language.json";
        userIdsFile = "userIds.json";
        cartIdByUserIdFile = "cartIdsByUserId.json";
        try {
            languages = FileUtil.readMap(languageFile, UUID.class, String.class);
            userIds = FileUtil.readMap(userIdsFile, Long.class, UUID.class);
            cartIdByUserId = FileUtil.readMap(cartIdByUserIdFile, UUID.class, UUID.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract BotApiMethod<?> handler(Update update, ECommerceBot eCommerceBot);

    protected void saveToFile() throws IOException {
        FileUtil.write(userIdsFile, userIds);
        FileUtil.write(cartIdByUserIdFile, cartIdByUserId);
        FileUtil.write(languageFile, languages);
    }

    protected void saveLanguageToFile() throws IOException {
        FileUtil.write(languageFile, languages);
    }

    protected void saveCartIdToFile() throws IOException {
        FileUtil.write(cartIdByUserIdFile, cartIdByUserId);
    }
}
