package org.example.cloudfilestorage.service.minio;


import org.example.cloudfilestorage.dto.FileDto;

import java.io.InputStream;
import java.util.List;

public interface FileService {
    /**
     * Загрузка файла в Minio.
     *
     * @param fileDto данные файла для загрузки
     * @return информация о загруженном файле
     */
    FileDto uploadFile(FileDto fileDto);

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
