package ir.kbox.manager.controller;

import ir.kbox.manager.controller.dto.FileDto;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @GetMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public String showUploadPage(Model model, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent) {
        model.addAttribute("parent", parent);
        model.addAttribute("file", new File());
        return "file-upload";
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(File file, @RequestParam("uploadedImage")
            MultipartFile uploadedFile, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent) {
        file.setParent(parent.replace("\"", ""));
        fileService.uploadFile(file, uploadedFile);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String showMyFiles(Model model, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent) {
        model.addAttribute("files", fileService.findFilesInFolder(parent).stream()
                .map(FileDto::new).collect(Collectors.toList()));
        model.addAttribute("currentFolder", parent);
        model.addAttribute("explodedAddress", List.of(parent.split("/")));
        return "files";
    }

}
