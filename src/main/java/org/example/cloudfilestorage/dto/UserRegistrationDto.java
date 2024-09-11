package org.example.cloudfilestorage.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserRegistrationDto {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен состоять не менее чем из 8 символов")
    private String password;

    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть действительной")
    private String email;
}
