package ir.kbox.manager.model.file;

import ir.kbox.manager.model.BaseEntity;
import ir.kbox.manager.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "files")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class File extends BaseEntity {
    public static final String ROOT = "/";

    @EqualsAndHashCode.Include
    private String userId;
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
    private Set<String> sharedUserIds = new HashSet<>();

    public static File getRootFolder(User user) {
        return new File().setCreationDate(Instant.now())
                .setLastModified(Instant.now())
                .setParent(null)
                .setName(ROOT)
                .setIsDirectory(true)
                .setUserId(user.getId());
    }
}
