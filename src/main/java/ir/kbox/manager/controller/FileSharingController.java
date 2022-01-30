package ir.kbox.manager.controller;

import ir.kbox.manager.controller.dto.FullFileDto;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/files/share")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileSharingController {
    private final FileService fileService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showSharingPage(@RequestParam("id") String fileId, Model model) {
        model.addAttribute("fileId", fileId);
        FullFileDto file = fileService.getFileDetailsById(fileId);
        model.addAttribute("file", file);
        model.addAttribute("explodedAddress",
                List.of(("Root" + file.getParent()).split("/")));
        return "share";
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showSharedUsers(Model model) {
        List<User> users = fileService.findSharedUsersOfUser();
        model.addAttribute("users",users);
        return "shared-users";
    }
}
