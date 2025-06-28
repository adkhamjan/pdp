package uz.pdp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
public abstract class BaseModel {
    protected final UUID id;
    protected final LocalDateTime createdDate;
    @Setter
    protected boolean active;

    public BaseModel() {
        this.id = UUID.randomUUID();
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }

    public BaseModel(UUID id) {
        this.id = id;
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }
}
