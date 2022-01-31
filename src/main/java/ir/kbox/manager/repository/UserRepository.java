package ir.kbox.manager.repository;

import ir.kbox.manager.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByEmailOrUsername(String email, String username);

    Boolean existsUserByEmailOrUsername(String email, String username);

    boolean existsUserByUsername(String username);

    List<User> findUsersByIdIn(Collection<String> id);

    default Optional<User> findUserByEmailOrUsername(String input) {
        return findUserByEmailOrUsername(input, input);
    }

}
