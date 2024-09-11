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

    @NotBlank(message = "Заголовок файла обязателен")
    @Size(min = 1, max = 100, message = "Заголовок файла не должен превышать 100 символов")
    private String title;

    @Size(max = 500, message = "Описание файла не должно превышать 500 символов")
    private String description;

    @Size(max = 500, message = "Путь расположение файла")
    private String filepath;

    @SuppressWarnings("java:S1948")
    @NotNull(message = "Файл обязателен")
    private MultipartFile file;

    // TO DO
    @Size(max = 2048, message = "URL файла не должен превышать 2048 символов")
    private String url;

    private Long size;

    @Size(max = 255, message = "Имя файла не должно превышать 255 символов")
    private String filename;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}