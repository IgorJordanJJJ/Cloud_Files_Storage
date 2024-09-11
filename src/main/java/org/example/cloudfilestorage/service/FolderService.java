package org.example.cloudfilestorage.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.model.foledr.EFolder;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.FolderRepository;
import org.springframework.stereotype.Service;

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
}
