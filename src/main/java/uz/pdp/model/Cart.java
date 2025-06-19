package uz.pdp.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Cart extends BaseModel {
    private UUID userId;
    private List<CartItem> cartList;

    public Cart(UUID userId) {
        super();
        cartList = new ArrayList<>();
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                '}';
    }
}
