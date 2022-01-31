package ir.kbox.manager.repository;

import ir.kbox.manager.model.user.BaseUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @Test
    void findFilesBySharedUsersContaining() {
//        fileRepository.findFilesBySharedUsersContaining(BaseUser.create().setId("61f430f418581f1f16c66aa9").cast())
//                .forEach(System.out::println);

    }
}
