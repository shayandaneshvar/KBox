package ir.kbox.manager.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "users")
public class User extends BaseEntity {
    @NotEmpty
    @Size(min = 2)
    @Indexed(unique = true)
    private String username;
    @Email
    @Indexed(unique = true)
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private List<Roles> roles = new ArrayList<>();
}
