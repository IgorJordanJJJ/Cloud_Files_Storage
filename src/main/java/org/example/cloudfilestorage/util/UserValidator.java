package org.example.cloudfilestorage.util;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.service.ValidationUserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final ValidationUserService registrationUserService;
    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserRegistrationDto user = (UserRegistrationDto) target;

        // Проверка совпадения пароля и подтверждения пароля
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Пароли не совпадают");
        }

        // Проверка есть ли пользователь с таким именем
        if (registrationUserService.loadUserByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "Duplicate", "Пользователь с таким имененм существует");
        }

        // Проверка есть ли пользователь с таким email
        if (registrationUserService.loadUserByEmail(user.getEmail()).isPresent()) {
            errors.rejectValue("email", "Duplicate", "Пользователь с таким emial уже зарегистрирован");
        }

        // Дополнительные проверки (например, минимальная длина пароля)
//        if (user.getPassword().length() < 8) {
//            errors.rejectValue("password", "Length", "Пароль должен состоять не менее чем из 8 символов");
//        }


    }
}
