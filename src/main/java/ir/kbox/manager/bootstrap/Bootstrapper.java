package ir.kbox.manager.bootstrap;

import ir.kbox.manager.model.user.Roles;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.repository.UserRepository;
import ir.kbox.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Bootstrapper implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsUserByUsername("admin")) {
            userService.saveUser(new User()
                    .setFirstname("Admin name")
                    .setLastname("Admin Last name")
                    .setEmail("example@example.com")
                    .setUsername("admin")
                    .setPassword("password"),
                    List.of(Roles.USER, Roles.ADMIN));
        }
    }
}
