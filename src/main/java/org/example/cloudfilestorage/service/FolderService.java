package org.example.cloudfilestorage.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                .build();
        folder.updateDownloadUrlApp();
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

    public Folder findByFolderIdAndUserId(Integer folderId, User user) {
        return folderRepository.findByUserIdAndId(user.getId(), folderId).orElse(null);
    }

    public String getFullPathById(Integer folderId) {
        StringBuilder pathBuilder = new StringBuilder();
        buildPath(folderId, pathBuilder);
        return pathBuilder.length() > 0 ? pathBuilder.toString() : "/";
    }

    private void buildPath(Integer folderId, StringBuilder pathBuilder) {
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
            // Папка с таким ID не найдена, обработайте это как необходимо
            // Например, можно выбросить исключение или просто логгировать
            // throw new IllegalArgumentException("Folder with ID " + folderId + " not found");
        }
    }
}
