package ir.kbox.manager.controller;

import ir.kbox.manager.controller.dto.FullFileDto;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.BaseUser;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.FileService;
import ir.kbox.manager.service.UserService;
import ir.kbox.manager.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/files/share")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileSharingController {
    private final FileService fileService;
    private final UserService userService;

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


    @GetMapping("/users")
    @PreAuthorize("isAuthenticated()")
    public String showSharedUsers(Model model) {
        List<User> users = fileService.findSharedUsersOfUser();
        model.addAttribute("users", users);
        return "shared-users";
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showSharedUserFiles(@RequestParam("userId") String userId,
                                      @RequestParam(value = "folder",
                                              defaultValue = File.ROOT)
                                              String folder,
                                      @RequestParam(value = "parent",
                                              defaultValue = File.ROOT)
                                              String parent,
                                      Model model, HttpServletRequest req) {
        parent = Util.nullStringDefault(parent, File.ROOT);
        folder = Util.nullStringDefault(folder, File.ROOT);
        List<File> files = fileService.findSharedFilesFromUser(userId, parent, folder);
        model.addAttribute("files", files);
        model.addAttribute("explodedAddress",
                List.of(("Root" + parent + "/" + folder)
                        .replace("//", "/").split("/")));
        User user = userService.findUserById(userId);
        model.addAttribute("user", new BaseUser(user));
        model.addAttribute("sessionId", req.getSession().getId());
        model.addAttribute("count", files.size());
        return "shared-files";
    }
}
