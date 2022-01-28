package ir.kbox.manager.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@UtilityClass
public class FileUtil {
    public static final String uploadPath = "/home/files/";

    public Pair<String, String> uploadFile(@NonNull MultipartFile file, @NonNull String title) throws IOException {
        File path = new File(uploadPath + UUID.randomUUID() + "-" +
                title + getFileExtension(file.getOriginalFilename()));
        if (!path.createNewFile()) {
            log.warn("Path Creation was not successful!! - " +
                    path.getAbsolutePath());
        }
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(file.getBytes());
        }
        return Pair.of(path.getAbsolutePath(), getContentType(path));
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

    private String getFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }

    public String getFormat(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    public void readAndWrite(final InputStream is, final OutputStream os)
            throws IOException {
        byte[] data = new byte[2048];
        int read = 0;
        while ((read = is.read(data)) > 0) {
            os.write(data, 0, read);
        }
        os.flush();
    }
}
