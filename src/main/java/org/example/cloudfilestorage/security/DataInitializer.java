package org.example.cloudfilestorage.security;

import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.model.user.ERole;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.RoleRepository;
import org.example.cloudfilestorage.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Проверяем, есть ли уже админ
        if (userRepository.findByUsername("admin").isEmpty()) {

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            // Создаем нового админа
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(adminRole))
                    .enabled(true)
                    .build();

            userRepository.save(admin);
        }
    }
}
