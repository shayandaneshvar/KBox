package ir.kbox.manager.controller.webservice.rest;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.FileService;
import ir.kbox.manager.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/share")
public class FileSharingRestController {
    private final FileService fileService;
    private final SecurityUtil securityUtil;

    @GetMapping("/folder/update")
    @PreAuthorize("isAuthenticated()")
    public Boolean hasFolderUpdated(
            @RequestParam(value = "folder", defaultValue = File.ROOT)
                    String folder,
            @RequestParam("lastModified") Long lastModified) {
        if (folder.equals("null")) {
            folder = File.ROOT;
        }
        return fileService.hasSharedFolderUpdated(folder, lastModified);
    }

    @PutMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void shareWithUser(@RequestParam("username") String username,
                              @RequestParam("fileId") String fileId) {
        fileService.shareWithUser(fileId, username);
    }

    @PutMapping("/user/link")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void toggleVisibility(@RequestParam("fileId") String fileId) {
        fileService.toggleVisibility(fileId);
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

    @GetMapping("/download/{id}/link")
    public ResponseEntity<StreamingResponseBody> downloadFileWithLink(@PathVariable("id") String fileId,
                                                                      @RequestHeader(value = "Range",
                                                                              required = false) String rangeHeader) {
        return fileService.getFileDownloadStreamWithLink(fileId, rangeHeader);
    }
}
