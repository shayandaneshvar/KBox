package ir.kbox.manager.repository;

import ir.kbox.manager.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByEmailOrUsername(String username, String email);
    boolean existsUserByUsername(String username);
}
