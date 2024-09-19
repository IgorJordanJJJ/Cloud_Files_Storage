package org.example.cloudfilestorage.service;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.mapper.UserMapper;
import org.example.cloudfilestorage.model.foledr.Folder;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public User createDefaultUser(UserRegistrationDto userRegistrationDto, Role defaultRole) {
        // Маппим UserRegistration в User с ролью
        User user = userMapper.toUser(userRegistrationDto, passwordEncoder, defaultRole);

        // Сохраняем пользователя в базе данных
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Folder getRootFolder(User user) {
        return user.getFolders().stream()
                .filter(folder -> folder.getParentFolder() == null) // Фильтруем только корневые папки
                .findFirst() // Берем первую корневую папку
                .orElse(null); // Если корневых папок нет, возвращаем null
    }
}
