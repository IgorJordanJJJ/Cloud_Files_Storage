package org.example.cloudfilestorage.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.dto.OldFileDto;
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

    public void deletedFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }


    public Boolean fileExistsByFileIdAndUserId(Long fileId, Long userId) {
        return fileRepository.findByUserIdAndId(fileId, userId).orElse(null) != null;
    }

    public File findByIdAndUserId(Long fileId, Long userId) {
        fileExists(fileId, userId);
        return fileRepository.findByUserIdAndId(fileId, userId).orElse(null);
    }

    private void fileExists(Long fileId, Long userId) {
        if(!fileExistsByFileIdAndUserId(fileId, userId)){
            throw new EntityNotFoundException("Folder with ID " + fileId + " not found");
        }
    }

    public OldFileDto renameFile(Long fileId, String newName, Long userId) {
        fileExists(fileId, userId);
        File oldFile = findByIdAndUserId(fileId, userId);
        String oldFileName = oldFile.getFilename();
        oldFile.setFilename(newName);
        fileRepository.save(oldFile);
        return OldFileDto.builder()
                .filename(oldFileName)
                .filePath(oldFile.getFilepath())
                .build();
    }
}
