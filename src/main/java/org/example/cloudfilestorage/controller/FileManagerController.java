package org.example.cloudfilestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.facade.FileManagerFacade;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
                             @RequestParam("folderId") Long folderId,
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
        return fileManagerFacade.downloadFile(fileId, authentication);
    }

    @PostMapping("/upload-folder")
    public String uploadFolder(@RequestParam("files") MultipartFile[] files,
                               @RequestParam("folderPath") String folderPath,
                               @RequestParam("folderId") Long folderId,
                               Authentication authentication) {
        fileManagerFacade.uploadFolder(files, folderPath, folderId, authentication);
        return "redirect:/";
    }


    // Создание новой папки
    @PostMapping("/folder/create")
    public String createFolder(@RequestParam String folderName,
                               @RequestParam(required = false) Long parentId,
                               Authentication authentication) {
        String path = fileManagerFacade.createFolder(folderName, parentId, authentication);
        return "redirect:/?path=" + path;
    }

    // Удаление папки
    @PostMapping("/folder/delete")
    public String deleteFolder(@RequestParam Long folderId,
                               Authentication authentication) {
        String path = fileManagerFacade.deleteFolder(folderId, authentication);
        return "redirect:/?path=" + path;
    }

    // Переименование папки
    @PostMapping("/folder/rename")
    public String renameFolder(@RequestParam("folderId") Long folderId,
                               @RequestParam("newName") String newName,
                               Authentication authentication) {
        String path = fileManagerFacade.renameFolder(folderId, newName, authentication);
        return "redirect:/?path=" + path;
    }


}
