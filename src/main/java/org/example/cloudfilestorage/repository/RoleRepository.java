package org.example.cloudfilestorage.repository;

import org.example.cloudfilestorage.model.user.ERole;
import org.example.cloudfilestorage.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
