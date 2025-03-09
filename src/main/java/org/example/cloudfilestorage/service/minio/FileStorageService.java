package org.example.cloudfilestorage.service.minio;


import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.model.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileStorageService {
    /**
     * Загрузка файла в Minio.
     *
     * @param file данные файла для загрузки
     */
    void uploadFile(FileDto file);

    /**
     * Загрузка файла из Minio по имени файла.
     *
     * @param file файл для загрузки
     * @return Resource с содержимым файла
     */
    Resource downloadFile(File file);

    /**
     * Удаление файла из Minio.
     *
     * @param file файл для удаления
     */
    void deleteFile(File file);

    /**
     * Проверка существования файла в Minio по имени файла.
     *
     * @param filename имя файла для проверки
     * @return true, если файл существует, иначе false
     */
    boolean fileExists(String filename);

    /**
     * Генерация предподписанного URL для файла в Minio.
     *
     * @param filename имя файла
     * @return предподписанный URL
     */
    String generatePresignedUrlMinio(String filename);

    /**
     * Получение списка всех файлов в Minio.
     *
     * @return список файлов
     */
    List<FileDto> listFiles();
}
