package org.example.cloudfilestorage.controller;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.facade.FileManagerFacade;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileManagerController {

    private final FileManagerFacade fileManagerFacade;

    @GetMapping("/")
    public String index(@RequestParam(name = "path", required = false) String path, Model model, Authentication authentication) {
        fileManagerFacade.inflateModel(authentication, model, path);
        return "main/index";
    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model, Authentication authentication) {
        fileManagerFacade.search(authentication, query, model);
        return "searchResults";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("folderId") Integer folderId,
                             Authentication authentication) {
        fileManagerFacade.uploadFile(file, folderId, authentication);
        return "redirect:/";
    }

    @PostMapping("/file/delete")
    public String deleteFile(@RequestParam("fileId") Long fileId,
                             Authentication authentication) {
        fileManagerFacade.deleteFile(fileId, authentication);
        return "redirect:/";
    }

    @PostMapping("/file/rename")
    public String renameFile(@RequestParam("fileId") Long fileId,
                             @RequestParam("newName") String newName,
                             Authentication authentication) {
        fileManagerFacade.renameFile(fileId, newName, authentication);
        return "redirect:/";
    }

    // Скачивание файла
    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId,
                                                 Authentication authentication) {
        Resource file = fileManagerFacade.downloadFile(fileId, authentication);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/upload-folder")
    public String uploadFolder(@RequestParam("files") MultipartFile[] files,
                               @RequestParam("folderPath") String folderPath,
                               @RequestParam("folderId") Integer folderId,
                               Authentication authentication) {
        fileManagerFacade.uploadFolder(files, folderPath, folderId, authentication);
        return "redirect:/";
    }


    // Создание новой папки
    @PostMapping("/folder/create")
    public String createFolder(@RequestParam String folderName,
                               @RequestParam(required = false) String path,
                               Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        fileManagerFacade.createFolder(folderName, path, userDetails.getUsername());
        return "redirect:/?path=" + path;
    }

    // Удаление папки
    @PostMapping("/folder/delete")
    public String deleteFolder(@RequestParam Integer folderId,
                               @RequestParam(required = false) String path,
                               Authentication authentication) {
        fileManagerFacade.deleteFolder(folderId);
        return "redirect:/?path=" + path;
    }

    // Переименование папки
    @PostMapping("/folder/rename")
    public String renameFolder(@RequestParam("folderId") Integer folderId,
                               @RequestParam("newName") String newName,
                               @RequestParam(required = false) String path,
                               Authentication authentication) {
        fileManagerFacade.renameFolder(folderId, newName);
        return "redirect:/?path=" + path;
    }


}
