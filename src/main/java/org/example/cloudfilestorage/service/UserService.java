package org.example.cloudfilestorage.service;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.mapper.UserMapper;
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


    public void createDefaultUser(UserRegistrationDto userRegistrationDto, Role defaultRole) {
        // Маппим UserRegistration в User с ролью
        User user = userMapper.toUser(userRegistrationDto, passwordEncoder, defaultRole);

        // Сохраняем пользователя в базе данных
        userRepository.save(user);
    }
}
