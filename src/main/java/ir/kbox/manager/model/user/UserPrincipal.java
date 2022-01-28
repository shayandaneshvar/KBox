package ir.kbox.manager.model.user;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserPrincipal extends User {
    private final ir.kbox.manager.model.user.User user;

    public UserPrincipal(ir.kbox.manager.model.user.User user) {
        super(user.getUsername(), user.getPassword(), user.getRoles());
        this.user = user;
    }
}
