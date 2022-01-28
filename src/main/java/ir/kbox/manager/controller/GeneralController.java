package ir.kbox.manager.controller;

import ir.kbox.manager.model.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralController {

    @GetMapping(path = {"/", "/index.html", "/index", "/home"})
    public String getIndexPage() {
        return "index";
    }

    @GetMapping(path= {"/adduser"})
    @PreAuthorize("hasRole('ADMIN')")
    public  String getAddUserPage(User user) {
        return "adduser";
    }
}