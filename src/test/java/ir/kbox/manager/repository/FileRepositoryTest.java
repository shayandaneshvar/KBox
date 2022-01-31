package ir.kbox.manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @Test
    void findFilesBySharedUsersContaining() {
        fileRepository.findFilesBySharedUserWithId("61f430f418581f1f16c66aa9")
                .forEach(System.out::println);
    }
}
