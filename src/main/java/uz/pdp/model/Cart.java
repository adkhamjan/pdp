package uz.pdp.model;

import lombok.*;

import java.util.UUID;


@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Cart extends BaseModel {
    private UUID cartId;
    private UUID userId;
    private UUID productId;
    private int quantity;

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", productId=" + productId +
                '}';
    }
}
