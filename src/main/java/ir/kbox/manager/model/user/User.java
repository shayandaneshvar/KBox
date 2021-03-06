package ir.kbox.manager.model.user;

import ir.kbox.manager.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Document(collection = "users")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class User extends BaseEntity {
    @NotEmpty(message = "username cannot be empty!")
    @Size(min = 2, message = "username must be at least 2 characters long!")
    @Indexed(unique = true)
    private String username;
    @Email(message = "Email is not valid!")
    @Indexed(unique = true)
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private List<Roles> roles = new ArrayList<>();
    private Instant creationDate;

    public boolean isAdmin() {
        return roles.contains(Roles.ADMIN);
    }
}
