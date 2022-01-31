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

    File findFileByNameAndParentAndUserId(String name, String parent, String userId);

    Optional<File> findByIdAndUserId(String id, String currentUser);

    Boolean existsByParent(String parent);

    List<File> findFilesByParentAndUserId(String parent, String currentUser);

    @Query(value = "{sharedUsers: {$elemMatch: {_id: ?0 }}}", fields = "{userId : 1}")
    List<File> findFilesBySharedUserWithId(String id);

    @Query(value = "{sharedUsers: {$elemMatch: {_id: ?0 }}}", count = true)
    long countFilesBySharedUserWithId(String id);


    @Query(value = "{$and: [{isDirectory: false},{_id: ?0},{userId: ?1 }, {parent: {$regex : ?2 }} ]}")
    Optional<File> findNonDirectoryFileByIdAndUserIdAndParentRegex(String fileId, String userId, String baseParent);

    default Optional<File> findNonDirectoryFileByIdAndUserIdAndParentStartWith(String fileId, String userId, String baseParent) {
        return findNonDirectoryFileByIdAndUserIdAndParentRegex(fileId, userId, "^" + baseParent);
    }

    @Query(value = "{$and: [ {userId: ?0 }, {sharedUsers: {$elemMatch: {_id: ?1 } } } ] }")
    List<File> findFilesByUserIdAndSharedUserId(String userId, String sharedUserId);

    default Set<String> findUserIdsBySharedUserWithId(String id) {
        return findFilesBySharedUserWithId(id)
                .stream()
                .map(File::getUserId)
                .collect(Collectors.toSet());
    }

    @Query(exists = true, value = "{ $and: [ { parent: ?0 }, { name: ?1}, { userId: ?2 }, { sharedUsers: { $elemMatch: { _id: ?3 } }}  ] }")
    Boolean existsByParentAndNameAndUserIdAndSharedUserId(String parent,
                                                          String folder,
                                                          String userId,
                                                          String sharedId);

    @Query(exists = true, value = "{ $and: [{isDirectory: false} , { parent: ?0 }," +
            " { name: ?1 }, { userId: ?2 }, { sharedUsers: { $elemMatch: { _id: ?3 } } } ] }")
    Boolean existsNonDirectoryByParentAndNameAndUserIdAndSharedUserId(
            String parent,
            String folder,
            String userId,
            String sharedId);

    @Query(value = "{ $and: [{isDirectory: false}, { userId: ?0 }, {parent: {$regex: ?1}} ] }")
    List<File> findNonDirectoryFilesWithUserIdAndParentRegex(String userId, String parent);

    @Query(value = "{$and: [{isDirectory: false}, { _id: ?0 }, { userId: ?1 }," +
            " { sharedUsers: { $elemMatch: {_id: ?2 } } }] }")
    Optional<File> findNonDirectoryFileByIdAndUserIdAndSharedUserId(String fileId,
                                                                    String userId,
                                                                    String sharedUserId);

    @Query(value = "{$and: [{isDirectory: false}, { _id: ?0 }, { userId: ?1 },{ sharedUsers: { $elemMatch: {_id: ?2 } } }] }", exists = true)
    Boolean existNonDirectoryFileByIdAndUserIdAndSharedUserId(String fileId, String id, String userId);

    default List<File> findNonDirectoryFilesWithUserIdAndParentStartsWith(String userId, String parent) {
        return findNonDirectoryFilesWithUserIdAndParentRegex(userId, "^" + parent);
    }

    @Query("{ $and: [ { parent: ?1 }, { name: ?0}, { sharedUsers: { $elemMatch: { _id: ?2 } }}] }")
    File findFileByNameAndParentAndSharedUserId(String name, String parent, String sharedUserId);
}
