package org.example.cloudfilestorage.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.OldFolderDto;
import org.example.cloudfilestorage.model.foledr.EFolder;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderService {
    private final FolderRepository folderRepository;

    public Folder createRootFolder(@NotNull User user) {
        String folderName = "user-" + user.getId() + "-files";
        Folder folder = Folder.builder()
                .name(folderName)
                .folderType(EFolder.SYSTEM)
                .user(user)
                .folderPath(folderName)
                .build();
        return folderRepository.save(folder);
    }

    public Folder getFolderByPath(User user, String path) {
        if (path == null || path.isEmpty()) {
            return folderRepository.findByUserIdAndParentFolderId(user.getId(), null).orElse(null);
        } else {
            return folderRepository.findByUserIdAndName(user.getId(), path).orElse(null);
        }
    }

    public List<Folder> getSubFolders(User user, Folder folder) {
        return folderRepository.findSubFolders(user.getId(), folder.getId());
    }

    public List<Folder> getBreadcrumb(Folder currentFolder) {
        List<Folder> breadcrumb = new ArrayList<>();
        Folder folder = currentFolder;

        // Проходим вверх по иерархии папок, добавляя их в список "хлебных крошек"
        while (folder != null) {
            breadcrumb.add(0, folder); // Вставляем папку в начало списка
            folder = folder.getParentFolder();
        }

        return breadcrumb;
    }

    public Boolean folderExistsByFileIdAndUserId(Long folderId, Long userId) {
        return folderRepository.findByUserIdAndId(folderId, userId).orElse(null) != null;
    }

    private void folderExists(Long folderId, Long userId) {
        if (!folderExistsByFileIdAndUserId(folderId, userId)) {
            throw new EntityNotFoundException("Folder with ID " + folderId + " not found");
        }
    }

    public Folder findByIdAndUserId(Long folderId, Long userId) {
        folderExists(folderId, userId);
        return folderRepository.findByUserIdAndId(folderId, userId).orElse(null);
    }

    public String getFullPathById(Long folderId) {
        StringBuilder pathBuilder = new StringBuilder();
        buildPath(folderId, pathBuilder);
        return pathBuilder.length() > 0 ? pathBuilder.toString() : "/";
    }

    private void buildPath(Long folderId, StringBuilder pathBuilder) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            if (folder.getParentFolder() != null) {
                buildPath(folder.getParentFolder().getId(), pathBuilder);
            }
            if (pathBuilder.length() > 0) {
                pathBuilder.append("/");
            }
            pathBuilder.append(folder.getName());
        } else {
            throw new IllegalArgumentException("Folder with ID " + folderId + " not found");
        }
    }

    public Folder createFolder(String folderName, Long parentId, User user) {
        Folder folder = folderRepository.findByIdAndUserId(parentId, user.getId()).orElse(null);
        if (folder == null) {
            throw new EntityNotFoundException("Folder with ID " + folder.getId() + " not found");
        }
        Folder newFolder = Folder.builder()
                .name(folderName)
                .folderType(EFolder.USER)
                .user(folder.getUser())
                .parentFolder(folder)
                .folderPath(folder.getFolderPath() + "/" + folderName)
                .build();
        return folderRepository.save(newFolder);

    }

    public void deleteFolder(Long folderId) {
        folderRepository.deleteById(folderId);
    }

    public OldFolderDto renameFolder(Long folderId, String newName, Long id) {
        folderExists(folderId, id);
        Folder oldFolder = findByIdAndUserId(folderId, id);
        String oldName = oldFolder.getFolderPath();
        int index = oldName.indexOf(oldFolder.getName());
        // Получаем родительский путь до папки
        String parentPath = oldName.substring(0, index);
        // Новый путь для папки с заменённым именем
        String newFolderPath = parentPath + newName;
        oldFolder.setName(newName);
        oldFolder.setFolderPath(newFolderPath);
        folderRepository.save(oldFolder);
        return OldFolderDto.builder()
                .newFolderPath(newFolderPath)
                .oldFolderPath(oldName)
                .build();
    }
}
