package ir.kbox.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralController {
    @GetMapping(path = {"/", "/index.html", "/index", "/home"})
    public String getIndexPage() {
        return "index";
    }


}
