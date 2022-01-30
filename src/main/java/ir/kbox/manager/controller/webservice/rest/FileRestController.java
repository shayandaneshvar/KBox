package ir.kbox.manager.controller.webservice.rest;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileRestController {
    private final FileService fileService;
    private final SecurityUtil securityUtil;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload/folder")
    @PreAuthorize("isAuthenticated()")
    public String createFolder(@RequestParam("name") String name, @RequestParam(
            value = "parent", defaultValue = File.ROOT) String parentFolder) {
        return fileService.createFolder(name, parentFolder);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String id,
                                                              @RequestParam("sessionId") String sessionId,
                                                              @RequestHeader(value = "Range",
                                                                      required = false) String rangeHeader) {
        User user = securityUtil.checkSessionAndGetUser(sessionId);
        return fileService.getFileDownloadStream(id, rangeHeader,user);
    }

    @GetMapping("/upload/file")
    @PreAuthorize("isAuthenticated()")
    public Boolean doesOverwrite(@RequestParam(value = "parent", defaultValue
            = File.ROOT) String parent, @RequestParam("name") String name) {
        return fileService.doesOverwrite(parent, name);
    }

    @GetMapping("/folder/update")
    @PreAuthorize("isAuthenticated()")
    public Boolean hasFolderUpdated(@RequestParam("folder") String folder,
                                    @RequestParam("lastModified") Instant lastModified) {
        return fileService.hasFolderUpdated(folder, lastModified);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFileById(@RequestParam("id") String id) {
        fileService.deleteFileById(id);
    }

}
