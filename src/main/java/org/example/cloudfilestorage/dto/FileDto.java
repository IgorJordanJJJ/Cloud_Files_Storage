package org.example.cloudfilestorage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FileDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 232836038145089522L;

    // Путь, где расположен файл (опционально)
    @Size(max = 500, message = "Путь к файлу не может превышать 500 символов.")
    private String filePath;

    // Сам файл, обязательный только для операций загрузки
    @NotNull(message = "Файл обязателен для загрузки.")
    private MultipartFile file;

    // URL для доступа к файлу, используется для скачивания или получения метаданных
    @Size(max = 2048, message = "URL файла не может превышать 2048 символов.")
    private String url;

    // Размер файла в байтах
    private Long size;

    // Имя файла с валидацией длины
    @NotBlank(message = "Имя файла не может быть пустым.")
    @Size(max = 255, message = "Имя файла не может превышать 255 символов.")
    private String filename;

    // Временные метки для отслеживания создания и обновления файла
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}