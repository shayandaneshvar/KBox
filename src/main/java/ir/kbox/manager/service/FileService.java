package ir.kbox.manager.service;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.controller.dto.FullFileDto;
import ir.kbox.manager.controller.exceptions.AlreadyExistsException;
import ir.kbox.manager.controller.exceptions.NotFoundException;
import ir.kbox.manager.controller.exceptions.OperationFailedException;
import ir.kbox.manager.controller.exceptions.UnacceptableRequestException;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.model.user.BaseUser;
import ir.kbox.manager.model.user.User;
import ir.kbox.manager.repository.FileRepository;
import ir.kbox.manager.repository.UserRepository;
import ir.kbox.manager.util.FileUtil;
import ir.kbox.manager.util.datastructure.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private static final int FILE_BUFFER_SIZE = 1024;

    @Transactional
    public String createFolder(String name, String parentFolder) {
        checkAndCreateRoot();
        if (parentFolder.trim().isEmpty()) {
            parentFolder = File.ROOT;
        }
        if (!parentFolder.equals(File.ROOT)) {
            if (checkFileOrFolderExists(parentFolder, name)) {
                throw new AlreadyExistsException("Folder with the same name already exists!");
            } else if (checkFolderDoesntExist(parentFolder)) {
                throw new NotFoundException("Such parent Folder does not exist!");
            }
        }
        String result = fileRepository.save(new File().setIsDirectory(true)
                .setLastModified(Instant.now())
                .setCreationDate(Instant.now())
                .setName(name).setParent(parentFolder)
                .setUserId(securityUtil.getCurrentUser().getId()))
                .getName();
        var parentAndName = extractParentAndFolder(parentFolder);
        fileRepository.save(fileRepository.findFileByNameAndParentAndUserId(parentAndName.getSecond()
                , parentAndName.getFirst(), securityUtil.getCurrentUser().getId())
                .setLastModified(Instant.now()));
        return result;
    }

    public boolean checkFileOrFolderExists(String parent, String folderName) {
        return fileRepository.existsFileByNameAndParentAndUserId(folderName, parent,
                securityUtil.getCurrentUser().getId());
    }

    public boolean checkFolderDoesntExist(String parentWithFolder) {
        Tuple2<String, String> parentAndFolder = extractParentAndFolder(parentWithFolder);
        return !checkFileOrFolderExists(parentAndFolder.getFirst(), parentAndFolder.getSecond());
    }

    public static Tuple2<String, String> extractParentAndFolder(String parentWithFolder) {
        if (parentWithFolder == null || parentWithFolder.equals(File.ROOT)) {
            return Tuple2.of(null, File.ROOT);
        }
        String[] folders = parentWithFolder.split("/");
        String lastFolder = folders[folders.length - 1];
        String parent = parentWithFolder.substring(0,
                parentWithFolder.lastIndexOf(lastFolder));
        if (!parent.equals(File.ROOT)) {
            parent = parentWithFolder.substring(0, parent.length() - 1);
        }
        return Tuple2.of(parent, lastFolder);
    }

    @Transactional
    public void uploadFile(File file, MultipartFile uploadedFile) {
        checkAndCreateRoot();
        if (file.getParent() == null || file.getParent().trim().isEmpty()) {
            file.setParent(File.ROOT);
        }
        if (file.getParent() != null && !file.getParent()
                .equals(File.ROOT) &&
                checkFolderDoesntExist(file.getParent())) {
            throw new NotFoundException("Parent Folder does not exist!");
        }
        file.setName(uploadedFile.getOriginalFilename());
        if (checkFileOrFolderExists(file.getParent(), file.getName())) {
            File previousFile = fileRepository
                    .findFileByNameAndParentAndUserId(file.getName(),
                            file.getParent(), securityUtil.getCurrentUser().getId());
            file.setId(previousFile.getId());
            FileUtil.deleteFile(previousFile.getAddress());
            fileRepository.delete(previousFile);
        }
        Tuple2<String, String> addressContent;
        try {
            addressContent = FileUtil.uploadFile(uploadedFile, file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new OperationFailedException(e.getMessage());
        }
        file.setIsDirectory(false).setAddress(addressContent.getFirst())
                .setCreationDate(Instant.now())
                .setLastModified(Instant.now())
                .setFileType(addressContent.getSecond())
                .setUserId(securityUtil.getCurrentUser().getId());
        fileRepository.save(file);
        File folder;
        Tuple2<String, String> parentAndFolder = extractParentAndFolder(file.getParent());
        folder = fileRepository.findFileByNameAndParentAndUserId(parentAndFolder.getSecond(),
                parentAndFolder.getFirst(), securityUtil.getCurrentUser().getId());
        folder.setLastModified(Instant.now());
        fileRepository.save(folder);
    }

    private void checkAndCreateRoot() {
        if (!fileRepository.existsFileByNameAndUserId(File.ROOT,
                securityUtil.getCurrentUser().getId())) {
            fileRepository.save(File.getRootFolder(securityUtil.getCurrentUser()));
        }
    }

    public Boolean hasFolderUpdated(String folder, Instant lastModified) {
        if (checkFolderDoesntExist(folder)) {
            throw new NotFoundException("Such folder does not exist!");
        }
        Tuple2<String, String> parentAndFolder = extractParentAndFolder(folder);
        return fileRepository.findFileByNameAndParentAndUserId(parentAndFolder.getSecond(),
                parentAndFolder.getFirst(), securityUtil.getCurrentUser().getId()).getLastModified().isAfter(lastModified);
    }

    public void deleteFileById(String id) {
        fileRepository
                .findByIdAndUserId(id, securityUtil.getCurrentUser().getId())
                .ifPresentOrElse(z -> {
                    if (z.getIsDirectory()
                            && fileRepository
                            .existsByParent(z.getParent() + "/" + z.getName())) {
                        throw new UnacceptableRequestException("This folder is not empty");
                    }
                    if (!z.getIsDirectory()) {
                        FileUtil.deleteFile(z.getAddress());
                    }
                    fileRepository.delete(z);
                }, () -> {
                    throw new NotFoundException("No such file exists!");
                });
    }

    public Boolean doesOverwrite(String parent, String name) {
        if (parent.equals("null")) {
            parent = File.ROOT;
        }
        return fileRepository.existsFileByNameAndParentAndUserId(name, parent, securityUtil.getCurrentUser().getId());
    }

    public List<File> findFilesInFolder(String parent) {
        checkAndCreateRoot();
        return fileRepository.findFilesByParentAndUserId(parent,
                securityUtil.getCurrentUser().getId());
    }

    public ResponseEntity<StreamingResponseBody> getFileDownloadStream(String id
            , String rangeHeader, User user) {
        File file = fileRepository.findByIdAndUserId(id,
                user.getId()).orElseThrow(NotFoundException::new);
        return streamFile(file, rangeHeader);
    }

    public ResponseEntity<StreamingResponseBody> getFileDownloadStream(String fileId,
                                                                       String rangeHeader,
                                                                       User user,
                                                                       String parent,
                                                                       String folder,
                                                                       String userId) {
        // base case
        if (fileRepository.existNonDirectoryFileByIdAndUserIdAndSharedUserId(fileId, user.getId(), userId)) {
            File file = fileRepository
                    .findNonDirectoryFileByIdAndUserIdAndSharedUserId(fileId, user.getId(), userId)
                    .orElseThrow(NotFoundException::new);
            return streamFile(file, rangeHeader);
        }
        // in folder case
        Tuple2<String, String> parentAndFolder = extractParentAndFolder(parent);
        if (fileRepository
                .existsByParentAndNameAndUserIdAndSharedUserId(
                        parentAndFolder.getFirst(), parentAndFolder.getSecond(), userId,
                        user.getId())) {
            File file = fileRepository.findNonDirectoryFileByIdAndUserIdAndParentStartWith(
                    fileId, userId, parent)
                    .orElseThrow(NotFoundException::new);
            return streamFile(file, rangeHeader);
        }
        throw new UnacceptableRequestException();
    }


    private ResponseEntity<StreamingResponseBody> streamFile(
            File inputFile, long rangeStart, long rangeEnd, long fileSize,
            final HttpHeaders responseHeaders) {
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        responseHeaders.add("Content-Length", contentLength);
        responseHeaders.add("Accept-Ranges", "bytes");
        responseHeaders.add("Content-Range", "bytes" + " "
                + rangeStart + "-" + rangeEnd + "/" + fileSize);
        StreamingResponseBody responseStream = os -> {
            RandomAccessFile file = new RandomAccessFile(inputFile.getAddress(), "r");
            try (file) {
                long pos = rangeStart;
                file.seek(pos);
                while (pos < rangeEnd) {
                    file.read(buffer);
                    os.write(buffer);
                    pos += buffer.length;
                }
                os.flush();
            } catch (Exception e) {
                throw new NotFoundException("While Streaming file with id"
                        + inputFile.getId(), e);
            }
        };
        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }


    private ResponseEntity<StreamingResponseBody> streamFile(File inputFile,
                                                             long fileSize,
                                                             final HttpHeaders responseHeaders) {
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        responseHeaders.add("Content-Length", Long.toString(fileSize));
        StreamingResponseBody responseStream = os -> {
            RandomAccessFile file = new RandomAccessFile(inputFile.getAddress(), "r");
            try (file) {
                long pos = 0;
                file.seek(pos);
                while (pos < fileSize - 1) {
                    file.read(buffer);
                    os.write(buffer);
                    pos += buffer.length;
                }
                os.flush();
            } catch (Exception e) {
                throw new NotFoundException("While Streaming File with id"
                        + inputFile.getId(), e);
            }
        };
        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.OK);
    }

    private ResponseEntity<StreamingResponseBody> streamFile(File file, String rangeHeader) {
        long fileSize;
        try {
            fileSize = Files.size(Paths.get(file.getAddress()));
        } catch (IOException e) {
            throw new NotFoundException("While Streaming File with id - Sizing"
                    + file.getId(), e);
        }
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", file.getFileType());
        responseHeaders.add("Content-Disposition", "attachment;filename=" + file.getName());
        if (rangeHeader == null) {
            return streamFile(file, fileSize, responseHeaders);
        }
        String[] ranges = rangeHeader.split("-");
        long rangeStart = Long.parseLong(ranges[0].substring(6));
        long rangeEnd;
        if (ranges.length > 1) {
            rangeEnd = Long.parseLong(ranges[1]);
        } else {
            rangeEnd = fileSize - 1;
        }
        if (fileSize < rangeEnd) {
            rangeEnd = fileSize - 1;
        }
        return streamFile(file, rangeStart, rangeEnd, fileSize, responseHeaders);
    }

    public void renameFile(final String id, final String name) {
        fileRepository.findByIdAndUserId(id, securityUtil.getCurrentUser().getId())
                .ifPresentOrElse(file -> {
                            final String finalName = name + FileUtil.getFileExtension(file.getName());
                            if (checkFileOrFolderExists(file.getParent(), finalName)) {
                                throw new UnacceptableRequestException("Duplicate file with the same name exists!");
                            }
                            fileRepository.save(file.setName(finalName));
                        }
                        , () -> {
                            throw new NotFoundException();
                        });
    }

    public FullFileDto getFileDetailsById(String fileId) {
        File file = fileRepository.findByIdAndUserId(fileId,
                securityUtil.getCurrentUser().getId())
                .orElseThrow(NotFoundException::new);
        return new FullFileDto(file);
    }

    public void shareWithUser(String fileId, String username) {
        BaseUser user = userRepository
                .findUserByEmailOrUsername(username)
                .map(BaseUser::new)
                .orElseThrow(NotFoundException::new);

        File file = fileRepository
                .findByIdAndUserId(fileId, securityUtil.getCurrentUser().getId())
                .orElseThrow(NotFoundException::new);
        fileRepository.save(file.addToSharedUsers(user));
    }

    public void removeUserFromSharedUsers(String userId, String fileId) {
        File file = fileRepository
                .findByIdAndUserId(fileId, securityUtil.getCurrentUser().getId())
                .orElseThrow(NotFoundException::new);
        file.setSharedUsers(file.getSharedUsers().stream()
                .filter(z -> !z.getId().equals(userId))
                .collect(Collectors.toSet()));
        fileRepository.save(file);
    }

    public List<User> findSharedUsersOfUser() {
        Set<String> userIds = fileRepository
                .findUserIdsBySharedUserWithId(securityUtil.getCurrentUser().getId());
        return userRepository.findUsersByIdIn(userIds);
    }

    public List<File> findSharedFilesFromUser(String userId, String parent,
                                              String folder) {
        // list files and folder that are shared directly
        if (folder.equals(File.ROOT)) {
            return fileRepository.findFilesByUserIdAndSharedUserId(userId,
                    securityUtil.getCurrentUser().getId());
        }
        // list only files in chosen folder
        if (fileRepository.existsByParentAndNameAndUserIdAndSharedUserId(parent
                , folder, userId, securityUtil.getCurrentUser().getId())) {
            return fileRepository.findNonDirectoryFilesWithUserIdAndParentStartsWith(userId, (parent + "/" + folder)
                    .replace("//", "/"));
        }
        throw new UnacceptableRequestException();
    }


}
