package ir.kbox.manager.controller.dto;

import ir.kbox.manager.model.file.File;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@NoArgsConstructor
public class FileDto {
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
        if (parent == null) {
            parent = "";
        }
        enterFolder = parent + name + "/";
        previousFolder = parent.substring(0, parent.substring(0,
                parent.length() - 1).lastIndexOf("/") + 1);
    }
}
