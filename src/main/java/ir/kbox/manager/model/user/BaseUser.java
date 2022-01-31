package ir.kbox.manager.model.user;

import ir.kbox.manager.model.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class BaseUser extends BaseEntity {
    @Indexed
    private String username;
    @Indexed
    private String email;

    public BaseUser(User user) {
        this.setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setId(user.getId());
    }

    public static BaseUser create() {
        return new BaseUser();
    }
}
