package org.example.cloudfilestorage.service;

import lombok.AllArgsConstructor;
import org.example.cloudfilestorage.model.user.User;
import org.example.cloudfilestorage.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ValidationUserService {

    private final UserRepository userRepository;

    public Optional<User> loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
