package ir.kbox.manager.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal extends User {
    public UserPrincipal(ir.kbox.manager.model.User user) {
        super(user.getUsername(), user.getPassword(), user.getRoles());
    }
}
