package uz.pdp.BaceClass;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
public abstract class BaseModel {
    protected final UUID id;
    @Setter
    protected boolean active;

    public BaseModel() {
        this.id = UUID.randomUUID();
        this.active = true;
    }
}
