package uz.pdp.model;

import lombok.*;
import uz.pdp.BaceClass.BaseModel;

import java.util.UUID;


@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Cart extends BaseModel {
    private UUID userId;

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                '}';
    }
}
