package org.example.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.model.File;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public List<File> getFilesByFolder(User user, Folder folder) {
        return fileRepository.findByUserIdAndFolderId(user.getId(), folder.getId());
    }

    public FileDto uploadFile(MultipartFile requestFile, Folder folder, String filePath, User user) {
        File file = File.builder()
                .folder(folder)
                .user(user)
                .filepath(filePath)
                .filename(requestFile.getOriginalFilename())
                .size(requestFile.getSize())
                .build();
        fileRepository.save(file);
        return FileDto.builder()
                .file(requestFile)
                .filePath(filePath)
                .filename(requestFile.getOriginalFilename())
                .size(requestFile.getSize())
                .build();
    }

    public File checkAndDeletedFile(Long fileId, User user) {
        return null;
    }


    public Boolean fileExistsByFileId(Long fileId, User user) {
        if (fileRepository.findByUserAndId(user.getId(), fileId).orElse(null) != null) {
            return true;
        } else
            return false;
    }


}
