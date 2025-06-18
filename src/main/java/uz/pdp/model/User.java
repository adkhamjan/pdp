package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.BaceClass.BaseModel;
import uz.pdp.Enums.UserType;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class User extends BaseModel {
    private String name;
    private String userName;
    private String password;
    private UserType typeUser;
}