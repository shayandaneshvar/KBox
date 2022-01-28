package ir.kbox.manager.controller;

import ir.kbox.manager.model.file.File;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @GetMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public String showUploadPage(Model model) {
        model.addAttribute("file", new File());
        return "file-upload";
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(File file, @RequestParam("uploadedImage")
            MultipartFile uploadedFile, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent) {
        file.setParent(parent);
        fileService.uploadFile(file, uploadedFile);
    }

}
