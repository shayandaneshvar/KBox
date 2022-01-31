package ir.kbox.manager.controller.dto;

import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FullFileDto extends FileDto {
    private Set<BaseUser> sharedUsers = new HashSet<>();

    public FullFileDto(File file) {
        BeanUtils.copyProperties(file, this);
        loadFolderProperties(file);
    }
}
