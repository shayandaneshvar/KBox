package ir.kbox.manager.controller;

import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/files/share")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileSharingController {
    private final FileService fileService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showSharingPage(@RequestParam("id") String fileId, Model model) {
        model.addAttribute("fileId",fileId);
        // TODO: 1/30/2022  
        return "share";
    }
}
