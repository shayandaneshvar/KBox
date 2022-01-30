package ir.kbox.manager.controller.dto;

import ir.kbox.manager.model.file.File;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@NoArgsConstructor
public class FileDto {
    private String id;
    private String parent;
    private String previousFolder;
    private String enterFolder;
    private String name;
    private String fileType;
    private Boolean isDirectory;
    private Instant creationDate;
    private Instant lastModified;

    public FileDto(File file) {
        BeanUtils.copyProperties(file, this);
        loadFolderProperties();
    }

    protected void loadFolderProperties() {
        if (parent == null) {
            parent = "";
        } else if (!parent.equals(File.ROOT)) {
            parent = parent + "/";
        }

        enterFolder = parent + name;
        if (parent.isEmpty()) {
            previousFolder = "";
            return;
        }
        previousFolder = ExtractPreviousFolderFromParent(parent);
    }

    public static String ExtractPreviousFolderFromParent(String parent) {
        String result = parent.substring(0, parent.substring(0,
                parent.length() - 1).lastIndexOf("/") + 1);
        if (!result.isEmpty() && !result.equals(File.ROOT)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
