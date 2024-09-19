package org.example.cloudfilestorage.facade;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.model.File;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.service.FileService;
import org.example.cloudfilestorage.service.FolderService;
import org.example.cloudfilestorage.service.UserService;
import org.example.cloudfilestorage.service.minio.MinioStorageServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagerFacade {

    private final FileService fileService;
    private final FolderService folderService;
    private final MinioStorageServiceImpl minioService;
    private final UserService userService;

    public void inflateModel(Authentication authentication, Model model, String path) {
        if (isAuthenticated(authentication)) {
            // execption
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("currentUser", userDetails.getUsername());
        User user = userService.findByUsername(userDetails.getUsername());
        Folder currentFolder = folderService.getFolderByPath(user, path);
        List<File> files = fileService.getFilesByFolder(user, currentFolder);
        List<Folder> subFolders = folderService.getSubFolders(user, currentFolder);
        List<Folder> breadcrumb = folderService.getBreadcrumb(currentFolder);

        model.addAttribute("currentFolder", currentFolder);
        model.addAttribute("files", files);
        model.addAttribute("subFolders", subFolders);
        model.addAttribute("breadcrumb", breadcrumb);
    }

    public void search(Authentication authentication, String query, Model model) {
        if (isAuthenticated(authentication)) {
            // execption
        }
//        List<File> files = fileService.searchFiles(query);
//        List<Folder> folders = folderService.searchFolders(query);
//
//        model.addAttribute("searchResults", files);
//        model.addAttribute("searchFolders", folders);
    }

    public void uploadFile(MultipartFile requestFile, Integer folderId, Authentication authentication) {
        if (isAuthenticated(authentication))
        {
            // TO-DO
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Folder folder = folderService.findByFolderIdAndUserId(folderId, user);
        if(folder == null) {
            // TO-DO
        }
        String filePath = folderService.getFullPathById(folderId);
        FileDto fileDto = fileService.uploadFile(requestFile, folder, filePath, user);
        minioService.uploadFile(fileDto);
    }

    public void deleteFile(Long fileId, Authentication authentication) {


    }

    public void renameFile(Long fileId, String newName, Authentication authentication) {

    }

    public void createFolder(String folderName, String path, String username) {

    }

    public void deleteFolder(Integer folderId) {

    }


    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    public void renameFolder(Integer folderId, String newName) {
    }

    public Resource downloadFile(Long fileId, Authentication authentication) {
        return null;
    }

    public void uploadFolder(MultipartFile[] files, String folderPath, Integer folderId, Authentication authentication) {
    }
}
