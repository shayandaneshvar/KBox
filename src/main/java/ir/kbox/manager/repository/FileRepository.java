package ir.kbox.manager.repository;

import ir.kbox.manager.model.file.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

    Boolean existsFileByNameAndUserId(String name, String userId);

    Boolean existsFileByNameAndParentAndUserId(String name, String parent, String userId);

    Boolean existsFileByNameAndParent(String name, String parent);

    Boolean existsByNameAndParent(String name, String parent);

    List<File> findByNameAndParentAndUserId(String name, String parent, String userId);

    List<File> findByNameAndParent(String name, String parent);

    File findFileByNameAndParentAndUserId(String name, String parent, String userId);

    Optional<File> findByIdAndUserId(String id, String currentUser);

    Boolean existsByParent(String parent);

    List<File> findFilesByParentAndUserId(String parent, String currentUser);

    @Query(value = "{sharedUsers: {$elemMatch: {_id: ?0 }}}", fields = "{userId : 1}")
    List<File> findFilesBySharedUserWithId(String id);

    default Set<String> findUserIdsBySharedUserWithId(String id) {
        return findFilesBySharedUserWithId(id)
                .stream()
                .map(File::getUserId)
                .collect(Collectors.toSet());
    }

}
