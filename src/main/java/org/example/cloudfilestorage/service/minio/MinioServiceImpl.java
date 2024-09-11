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
import org.example.cloudfilestorage.model.foledr.EFolder;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
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
public class MinioServiceImpl implements FileService {
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
    public FileDto uploadFile(FileDto fileDto) {
        try {
            MultipartFile file = fileDto.getFile();
            String filename = fileDto.getFilename() != null ? fileDto.getFilename() : file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            return FileDto.builder()
                    .size(fileDto.getFile().getSize())
                    .url(getPreSignedUrlApp(fileDto.getFile().getOriginalFilename()))
                    .filename(fileDto.getFile().getOriginalFilename())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error while uploading file to Minio", e);
        }
    }

    @Override
    public InputStream downloadFile(String filename) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(filename).build());
        } catch (Exception e) {
            throw new RuntimeException("Error while downloading file from Minio", e);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(filename).build());
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
                    PutObjectArgs.builder().bucket(bucketName).object(folder.getName()+"/").stream(
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
}
