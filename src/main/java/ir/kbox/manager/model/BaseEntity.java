package ir.kbox.manager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseEntity implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    public <T extends BaseEntity> T cast() {
        return (T) this;
    }
}
