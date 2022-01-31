package ir.kbox.manager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BaseEntity implements Serializable {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    public <T extends BaseEntity> T cast() {
        return (T) this;
    }
}
