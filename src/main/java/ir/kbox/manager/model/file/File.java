package ir.kbox.manager.model.file;

import ir.kbox.manager.model.BaseEntity;
import ir.kbox.manager.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedList;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "files")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class File extends BaseEntity {
    public static final String ROOT = "/";
    @DBRef
    @EqualsAndHashCode.Include
    private User user;
    @Indexed
    @EqualsAndHashCode.Include
    private String parent;
    @Indexed
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private String fileType;
    private Boolean isDirectory;
    private String address;
    private Visibility visibility = Visibility.STRICT;
    private Instant creationDate;
    private Instant lastModified;
}
