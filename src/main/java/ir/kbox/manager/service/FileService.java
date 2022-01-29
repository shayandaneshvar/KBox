package ir.kbox.manager.service;

import ir.kbox.manager.config.security.SecurityUtil;
import ir.kbox.manager.controller.exceptions.AlreadyExistsException;
import ir.kbox.manager.controller.exceptions.NotFoundException;
import ir.kbox.manager.controller.exceptions.OperationFailedException;
import ir.kbox.manager.model.file.File;
import ir.kbox.manager.repository.FileRepository;
import ir.kbox.manager.util.FileUtil;
import ir.kbox.manager.util.datastructure.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final SecurityUtil securityUtil;

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
        return fileRepository.save(new File().setIsDirectory(true)
                        .setName(name).setParent(parentFolder)
                        .setUserId(securityUtil.getCurrentUser().getId()))
                .getName();
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
                            .existsByParent(z.getParent() + "/" + z.getName() + "/")) {
                        throw new OperationFailedException("This folder is not empty");
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
}
