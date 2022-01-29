package ir.kbox.manager.controller;

import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegister(User user) {
        return "register";
    }

    // TODO: 1/29/2022 fix validation
    @PostMapping("/register")
    public String register(@Valid User user, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            System.out.println("sadsd");
            return "redirect:" + request.getRequestURL();
        }
        userService.registerUser(user);
        System.out.println("redirecting...");
        return "redirect:/";
    }
    @GetMapping({"/login.html","/login"})
    public String loginPage() {
        return "login";
    }


}
