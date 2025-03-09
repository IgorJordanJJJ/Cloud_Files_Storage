package org.example.cloudfilestorage.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OldFileDto implements Serializable {
    // Путь, где расположен файл (опционально)
    @Size(max = 500, message = "Путь к файлу не может превышать 500 символов.")
    private String filePath;

    // Имя файла с валидацией длины
    @NotBlank(message = "Имя файла не может быть пустым.")
    @Size(max = 255, message = "Имя файла не может превышать 255 символов.")
    private String filename;
}
