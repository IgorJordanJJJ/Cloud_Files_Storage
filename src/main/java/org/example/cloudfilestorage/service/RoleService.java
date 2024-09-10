package org.example.cloudfilestorage.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudfilestorage.model.user.ERole;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleDefaultUser() {
        return roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }

}
