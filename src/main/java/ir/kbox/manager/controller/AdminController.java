package ir.kbox.manager.controller;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SessionRegistry sessionRegistry;
    private final SecurityUtil securityUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String showUsers(Model model, HttpServletRequest request) {
        List<User> users = userService.findAll();
        model.addAttribute("usersList", users);
        model.addAttribute("sessionId", request.getSession()
                .getId());
        return "admin-panel";
    }

    @GetMapping(path = {"/user"})
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddUserPage(User user) {
        return "adduser";
    }

    @PutMapping(path = "/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changePassword(@PathVariable String id, @RequestParam("password") String password) {
        userService.changePassword(id, password);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
    }

}
