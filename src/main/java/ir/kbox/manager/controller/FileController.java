package ir.kbox.manager.controller;

import ir.kbox.manager.controller.dto.FileDto;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
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
    public String uploadFile(File file, @RequestParam("uploadedImage")
            MultipartFile uploadedFile, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent, HttpServletRequest req,
                             HttpServletResponse res) {
        file.setParent(parent.replace("\"", ""));
        fileService.uploadFile(file, uploadedFile);
//        res.setHeader("Location","redirect:/" + Util.getRequestBaseAddress(req) + "?parent=" + parent);
//        res.setStatus(302);
        return "redirect:/files?parent=" + parent;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String showMyFiles(Model model, @RequestParam(value = "parent",
            defaultValue = File.ROOT) String parent, HttpServletRequest req) {
        var files = fileService.findFilesInFolder(parent).stream()
                .map(FileDto::new).collect(Collectors.toList());
        model.addAttribute("files", files);
        String previousFolder = files.stream().map(FileDto::getPreviousFolder)
                .findFirst().orElse(FileDto.ExtractPreviousFolderFromParent(parent));
        model.addAttribute("previousFolder", previousFolder);
        model.addAttribute("currentFolder", parent);
        model.addAttribute("explodedAddress",
                List.of(("Root" + parent).split("/")));
        model.addAttribute("sessionId", req.getSession().getId());
        model.addAttribute("pageLoadDate", Instant.now());
        return "files";
    }

}
