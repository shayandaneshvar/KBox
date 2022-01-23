package ir.kbox.manager.service;

import ir.kbox.manager.model.Roles;
import ir.kbox.manager.model.User;
import ir.kbox.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(Roles.USER));
        return userRepository.save(user);
    }
}
