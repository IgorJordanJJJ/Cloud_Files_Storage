package org.example.cloudfilestorage.facade;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.dto.OldFileDto;
import org.example.cloudfilestorage.dto.OldFolderDto;
import org.example.cloudfilestorage.model.File;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.service.FileService;
import org.example.cloudfilestorage.service.FolderService;
import org.example.cloudfilestorage.service.UserService;
import org.example.cloudfilestorage.service.minio.MinioStorageServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

//        List<File> files = fileService.searchFiles(query);
//        List<Folder> folders = folderService.searchFolders(query);
//
//        model.addAttribute("searchResults", files);
//        model.addAttribute("searchFolders", folders);
    }

    public void uploadFile(MultipartFile requestFile, Long folderId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Folder folder = folderService.findByIdAndUserId(folderId, user.getId());
        String filePath = folderService.getFullPathById(folderId);
        FileDto fileDto = fileService.uploadFile(requestFile, folder, filePath, user);
        minioService.uploadFile(fileDto);
    }

    public void deleteFile(Long fileId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        File file = fileService.findByIdAndUserId(fileId, user.getId());
        fileService.deletedFile(fileId);
        minioService.deleteFile(file);
    }

    public void renameFile(Long fileId, String newName, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        OldFileDto oldFileDto = fileService.renameFile(fileId, newName, user.getId());
        minioService.renameFile(oldFileDto, newName);
    }

    public String createFolder(String folderName, Long parentId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Folder folder = folderService.createFolder(folderName, parentId, user);
        String pathRedirect = folder.getParentFolder().getFolderPath();
        minioService.createFolder(folder, folder.getFolderPath());
        return pathRedirect;
    }

    public String deleteFolder(Long folderId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Folder folder = folderService.findByIdAndUserId(folderId, user.getId());
        String pathRedirect = folder.getParentFolder().getFolderPath();
        folderService.deleteFolder(folderId);
        minioService.deleteFolderByPath(folder.getFolderPath());
        return pathRedirect;
    }

    public String renameFolder(Long folderId, String newName, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Folder folder = folderService.findByIdAndUserId(folderId, user.getId());
        OldFolderDto oldFolderDto = folderService.renameFolder(folderId, newName, user.getId());
        minioService.renameFolder(oldFolderDto);
        return folder.getParentFolder().getFolderPath();
    }

    public ResponseEntity<Resource> downloadFile(Long fileId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        File file = fileService.findByIdAndUserId(fileId, user.getId());
        Resource resource = minioService.downloadFile(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .body(resource);
    }

    public void uploadFolder(MultipartFile[] files, String folderPath, Long folderId, Authentication authentication) {
    }
}
