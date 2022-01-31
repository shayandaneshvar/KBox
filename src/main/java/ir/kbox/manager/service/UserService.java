package ir.kbox.manager.service;

import ir.kbox.manager.controller.exceptions.NotFoundException;
import ir.kbox.manager.controller.exceptions.UnacceptableRequestException;
import ir.kbox.manager.model.user.Roles;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.repository.UserRepository;
import ir.kbox.manager.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        return saveUser(user, List.of(Roles.USER));
    }

    public User saveUser(User user, List<Roles> roles) {
        if (userRepository.existsUserByEmailOrUsername(user.getUsername(), user.getEmail())) {
            throw new UnacceptableRequestException("Duplicate Email or username!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        user.setCreationDate(Instant.now());
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    public void changePassword(String id, String password) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public User findUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundException::new);
    }
}
