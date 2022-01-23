package ir.kbox.manager.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseEntity implements Serializable {
    @Id
    private String id;

}
