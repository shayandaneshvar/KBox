package ir.kbox.manager.controller.webservice.rest;

import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/share")
public class FileSharingRestController {
    private final FileService fileService;

    @PutMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void shareWithUser(@RequestParam("username") String username,
                              @RequestParam("fileId") String fileId) {
        fileService.shareWithUser(fileId, username);
    }

    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromSharedUsers(@RequestParam("userId") String userId,
                                      @RequestParam("fileId") String fileId) {
        fileService.removeUserFromSharedUsers(userId, fileId);
    }
}
