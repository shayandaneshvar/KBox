package ir.kbox.manager.controller.webservice.rest;

import ir.kbox.manager.model.file.File;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileRestController {
    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload/folder")
    @PreAuthorize("hasAnyRole()")
    public String createFolder(@RequestParam("name") String name, @RequestParam(
            value = "parent", defaultValue = File.ROOT) String parentFolder) {
        return fileService.createFolder(name, parentFolder);
    }

    @GetMapping("/upload/file")
    @PreAuthorize("isAuthenticated()")
    public Boolean doesOverwrite(@RequestParam(value = "parent", defaultValue
            = File.ROOT) String parent, @RequestParam("name") String name, HttpServletRequest httpServletRequest) {
        return fileService.doesOverwrite(parent, name);
    }

    @GetMapping("/folder/update")
    @PreAuthorize("hasAnyRole()")
    public Boolean hasFolderUpdated(@RequestParam("folder") String folder,
                                    @RequestParam("lastModified") Instant lastModified) {
        return fileService.hasFolderUpdated(folder, lastModified);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFileById(@RequestParam("id") String id) {
        fileService.deleteFileById(id);
    }

}
