package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.enums.UserType;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class User extends BaseModel {
    private String name;
    private String userName;
    private String phoneNumber;
    private UserType typeUser;
}