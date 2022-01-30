package ir.kbox.manager.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @Test
    void findFilesBySharedUsersContaining() {
//        fileRepository.findFilesBySharedUsersContaining()
    }
}
