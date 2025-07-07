package uz.pdp.bot;

import lombok.Getter;
import lombok.Setter;

import java.util.Stack;
import java.util.UUID;

@Getter @Setter
public class BotState {
    private int productCount;
    private String language;
    private Stack<UUID> userCategoryHistory;

    public BotState() {
        productCount = 1;
        language = "uzbek";
        userCategoryHistory = new Stack<>();
    }
}
