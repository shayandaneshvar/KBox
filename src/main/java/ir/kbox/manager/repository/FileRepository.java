package ir.kbox.manager.repository;

import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

    Boolean existsFileByNameAndUser(String name, User user);

    Boolean existsFileByNameAndParentAndUser(String name, String parent, User user);

    File findFileByNameAndParentAndUser(String name, String parent, User user);

    Optional<File> findByIdAndUser(String id, User currentUser);

    Boolean existsByParent(String parent);

}
