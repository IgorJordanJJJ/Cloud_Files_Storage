package org.example.cloudfilestorage.facade.auth;

import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.service.RoleService;
import org.example.cloudfilestorage.service.UserService;
import org.example.cloudfilestorage.util.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserValidator userValidator;
    private final UserService userService;
    private final RoleService roleService;


    // Need finish add create folder for user
    public String registerUser(UserRegistrationDto user, BindingResult bindingResult){
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        Role defaultRole = roleService.getRoleDefaultUser();
        userService.createDefaultUser(user, defaultRole);

        return "redirect:/auth/login?register_success";
    }

}
