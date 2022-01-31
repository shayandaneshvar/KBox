package ir.kbox.manager.repository;

import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.User;
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

    //    List<File> findFilesBySharedUserIdAndParentStartsWith(String userId, String cursor);
    @Query(value = "{$and: [{userId: ?0 }, {parent: {$regex :\"^?1\"}} , {sharedUsers: {$elemMatch: {_id: ?2 }}]}")
    List<File> findFilesByUserIdAndParentStartsWithAndSharedUsersContainId(String ownerId, String cursor, String userId);

    @Query(value = "{$and: [ {userId: ?0 }, {sharedUsers: {$elemMatch: {_id: ?1 } } } ] }")
    List<File> findFilesByUserIdAndSharedUserId(String userId, String sharedUserId);

    default Set<String> findUserIdsBySharedUserWithId(String id) {
        return findFilesBySharedUserWithId(id)
                .stream()
                .map(File::getUserId)
                .collect(Collectors.toSet());
    }

    @Query(exists = true,value = "{ $and: [{ parent: ?0 }," +
            " { name: ?1 }, { userId: ?2 }, { sharedUsers: { $elemMatch: { _id: ?3 } } } ] }")
    boolean existsByParentAndNameAndUserIdAndSharedUserId(String parent,
                                                          String folder,
                                                          String userId,
                                                          String sharedId);

    @Query(value = "{ $and: [{isDirectory: false}, { userId: ?0 }, {parent: {$regex :\"^?1\"}} ] }")
    List<File> findNonDirectoryFilesWithUserIdAndParentStartsWith(String userId, String parent);

    @Query(value = "{$and: [{isDirectory: false}, { _id: ?0 }, { userId: ?1 }," +
            " { sharedUsers: { $elemMatch: {_id: ?2 } } }] }")
    Optional<File> findNonDirectoryFileByIdAndUserIdAndSharedUserId(String fileId,
                                                                    String userId,
                                                                    String sharedUserId);
}
