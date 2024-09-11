package org.example.cloudfilestorage.facade.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.service.FolderService;
import org.example.cloudfilestorage.service.RoleService;
import org.example.cloudfilestorage.service.UserService;
import org.example.cloudfilestorage.service.minio.MinioServiceImpl;
import org.example.cloudfilestorage.util.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserValidator userValidator;
    private final UserService userService;
    private final RoleService roleService;
    private final MinioServiceImpl minioService;
    private final FolderService folderService;

    @Transactional
    public String registerUser(UserRegistrationDto userDto, BindingResult bindingResult) {
        userValidator.validate(userDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        Role defaultRole = roleService.getRoleDefaultUser();
        User user = userService.createDefaultUser(userDto, defaultRole);
        Folder folder =  folderService.createRootFolder(user);
        minioService.createRootFolder(folder);

        return "redirect:/auth/login?register_success";
    }

}
