package uz.pdp.bot.factory;

import java.util.List;

public class MenuName {
    private static final List<String> uzbName;
    private static final List<String> engName;
    private static final List<String> rusName;

    static {
        uzbName = List.of("Menyu", "Mening buyurtmalarim", "Savat", "Sozlamalar");
        engName = List.of("Menu", "My orders", "Cart", "Settings");
        rusName = List.of("Меню", "Мои заказы", "Корзина", "Настройки");
    }

    public static List<String> getUzbNames() {
        return uzbName;
    }

    public static List<String> getEngNames() {
        return engName;
    }

    public static List<String> getRusNames() {
        return rusName;
    }
}
