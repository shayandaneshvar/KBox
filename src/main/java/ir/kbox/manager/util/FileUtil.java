package ir.kbox.manager.util;

import ir.kbox.manager.util.datastructure.Tuple2;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@UtilityClass
public class FileUtil {
    public static final String uploadPath = "/home/files/";

    public Tuple2<String, String> uploadFile(@NonNull MultipartFile file, @NonNull String title) throws IOException {
        File path = new File(uploadPath + UUID.randomUUID() + "-" +
                title + getFileExtension(file.getOriginalFilename()));
        if (!path.createNewFile()) {
            log.warn("Path Creation was not successful!! - " +
                    path.getAbsolutePath());
        }
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(file.getBytes());
        }
        return Tuple2.of(path.getAbsolutePath(), getContentType(path));
    }

    private String getContentType(File path) {
        try {
            return Files.probeContentType(path.toPath());
        } catch (IOException e) {
            log.warn(e.getMessage());
            return "";
        }
    }

    public boolean deleteFile(String url) {
        if (url == null) {
            return true;
        }
        File file = new File(url);
        return file.delete();
    }

    public String getFileExtension(String originalFilename) {
        return originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";
    }
}
