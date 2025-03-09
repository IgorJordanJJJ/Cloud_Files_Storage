package org.example.cloudfilestorage.service.minio;


import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.FileDto;
import org.example.cloudfilestorage.dto.OldFileDto;
import org.example.cloudfilestorage.dto.OldFolderDto;
import org.example.cloudfilestorage.model.File;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    private void createBucketIfNotExists() {
        try {
            boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isBucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Бакет {} успешно создан", bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking/creating bucket in Minio", e);
        }
    }

    @Override
    public void uploadFile(FileDto fileDto) {
        try {
            MultipartFile file = fileDto.getFile();
            String filename = fileDto.getFilename();
            String filePath = fileDto.getFilePath(); // Получаем путь

            // Конструируем полный путь для объекта в MinIO
            String objectName = (filePath != null && !filePath.isEmpty()) ? filePath + "/" + filename : filename;

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

        } catch (Exception e) {
            throw new RuntimeException("Error while uploading file to Minio", e);
        }
    }

    @Override
    public Resource downloadFile(File file) {

        String filename = file.getFilename();
        String filePath = file.getFilepath();

        // Конструируем полный путь для объекта в MinIO
        String objectName = (filePath != null && !filePath.isEmpty()) ? filePath + "/" + filename : filename;

        try {
            // Get the file from MinIO
            InputStream fileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Wrap the InputStream in a Resource and return it
            return new InputStreamResource(fileStream);
        } catch (MinioException e) {
            e.printStackTrace();
            throw new RuntimeException("Error downloading file from MinIO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred while downloading file.");
        }
    }

    @Override
    public void deleteFile(File file) {
        String filename = file.getFilename();
        String filePath = file.getFilepath();

        // Конструируем полный путь для объекта в MinIO
        String objectName = (filePath != null && !filePath.isEmpty()) ? filePath + "/" + filename : filename;

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting file from Minio", e);
        }
    }

    public void renameFile(OldFileDto oldFileDto, String newFilepath) {
        String oldFilename = oldFileDto.getFilename();
        String filePath = oldFileDto.getFilePath();

        // Конструируем полный путь для объекта в MinIO
        String odlObjectName = (filePath != null && !filePath.isEmpty()) ? filePath + "/" + oldFilename : oldFilename;
        String objectName = (filePath != null && !filePath.isEmpty()) ? filePath + "/" + newFilepath : newFilepath;

        // 1. Скопировать файл с новым именем
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName) // новое имя (куда копировать)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(odlObjectName) // откуда копировать
                                    .build())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error while copy file from Minio", e);
        }
        // 2. Удалить старый файл
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(odlObjectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting file from Minio", e);
        }
    }

    @Override
    public boolean fileExists(String filename) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(filename).build());
            return true;
        } catch (Exception e) {
            if (e instanceof ErrorResponseException &&
                    ((ErrorResponseException) e).errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new RuntimeException("Error while checking file existence in Minio", e);
        }
    }

    public String generatePresignedUrlMinio(String filename) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filename)
                            .expiry(60 * 60 * 24) // 24 hours
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error while generating presigned URL", e);
        }
    }

    @Override
    public List<FileDto> listFiles() {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
            List<FileDto> fileDtos = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                FileDto fileDto = FileDto.builder()
                        .filename(item.objectName())
                        .size(item.size())
                        .url(generatePresignedUrlMinio(item.objectName()))
                        .build();
                fileDtos.add(fileDto);
            }
            return fileDtos;
        } catch (Exception e) {
            throw new RuntimeException("Error while listing files in Minio", e);
        }
    }

    private String getPreSignedUrlApp(String filename) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl.concat("/minio/download/").concat(filename);
    }

    public void createRootFolder(@NotNull Folder folder) {

        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(folder.getName() + "/").stream(
                                    InputStream.nullInputStream(), 0, -1)
                            .contentType("application/octet-stream")
                            .build());
            log.info("Папка {} успешно создана в бакете {}", folder.getName(), bucketName);

        } catch (MinioException e) {
            log.error("Ошибка при работе с MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать папку в MinIO", e);
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода при создании папки: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка ввода/вывода при создании папки", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании папки: {}", e.getMessage(), e);
            throw new RuntimeException("Неизвестная ошибка при создании папки в MinIO", e);
        }
    }

    public void createFolder(Folder folder, String path) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(path + "/").stream(
                                    InputStream.nullInputStream(), 0, -1)
                            .contentType("application/octet-stream")
                            .build());
            log.info("Папка {} успешно создана в бакете {}", folder.getName(), bucketName);

        } catch (MinioException e) {
            log.error("Ошибка при работе с MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать папку в MinIO", e);
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода при создании папки: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка ввода/вывода при создании папки", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании папки: {}", e.getMessage(), e);
            throw new RuntimeException("Неизвестная ошибка при создании папки в MinIO", e);
        }
    }

    public void deleteFolderByPath(String folderPath) {
        try {
            // Убираем возможный начальный/конечный слэш в пути
            if (folderPath.startsWith("/")) {
                folderPath = folderPath.substring(1);
            }
            if (!folderPath.endsWith("/")) {
                folderPath += "/";
            }

            // Список объектов в "папке"
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(folderPath).recursive(true).build());

            // Удаление каждого объекта
            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting folder from Minio", e);
        }
    }

    public void renameFolder(OldFolderDto oldFolderDto) {
        try {
            String oldFolderPath = oldFolderDto.getOldFolderPath();
            if (!oldFolderPath.endsWith("/")) {
                oldFolderPath += "/";
            }

            String newFolderPath = oldFolderDto.getOldFolderPath();
            if (!newFolderPath.endsWith("/")) {
                newFolderPath += "/";
            }

            log.info("newFolderPath: " + newFolderPath);

            // Список объектов в старой "папке"
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(oldFolderPath).recursive(true).build());

            // Копируем каждый объект в новый путь и удаляем из старого
            for (Result<Item> result : results) {
                Item item = result.get();
                String oldObjectName = item.objectName();
                String newObjectName = newFolderPath + oldObjectName.substring(oldFolderPath.length());

                // Копируем объект
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newObjectName)
                                .source(CopySource.builder().bucket(bucketName).object(oldObjectName).build())
                                .build()
                );

                // Удаляем объект из старой "папки"
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(oldObjectName).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while renaming folder in Minio", e);
        }
    }

}
