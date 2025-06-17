package uz.pdp.model;

import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Product extends BaseModel {
    private UUID categoryId;
    private String productName;
    private int price;
}
