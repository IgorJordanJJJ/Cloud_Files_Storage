package org.example.cloudfilestorage.service.minio;


import org.example.cloudfilestorage.dto.FileDto;
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
     * @param filename имя файла для загрузки
     * @return InputStream с содержимым файла
     */
    InputStream downloadFile(String filename);

    /**
     * Удаление файла из Minio по имени файла.
     *
     * @param filename имя файла для удаления
     */
    void deleteFile(String filename);

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
