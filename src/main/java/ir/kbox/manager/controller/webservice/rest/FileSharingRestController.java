package ir.kbox.manager.controller.webservice.rest;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.FileService;
import ir.kbox.manager.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/share")
public class FileSharingRestController {
    private final FileService fileService;
    private final SecurityUtil securityUtil;

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

    @GetMapping("/download/{id}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("id") String fileId,
                                                              @RequestParam("sessionId") String sessionId,
                                                              @RequestParam("parent") String parent,
                                                              @RequestParam("folder") String folder,
                                                              @RequestParam("userId") String userId,
                                                              @RequestHeader(value = "Range",
                                                                      required = false) String rangeHeader) {
        User user = securityUtil.checkSessionAndGetUser(sessionId);
        folder = Util.nullStringDefault(folder, File.ROOT);
        parent = Util.nullStringDefault(parent, File.ROOT);
        return fileService.getFileDownloadStream(fileId, rangeHeader, user, parent, folder, userId);
    }
}
