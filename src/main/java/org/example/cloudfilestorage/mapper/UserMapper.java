package org.example.cloudfilestorage.mapper;


import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.model.user.Role;
import org.example.cloudfilestorage.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface  UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "password", expression = "java( passwordEncoder.encode(userRegistrationDto.getPassword()) )")
    @Mapping(target = "roles", expression = "java( java.util.Set.of(role) )")
    User toUser(UserRegistrationDto userRegistrationDto, PasswordEncoder passwordEncoder, Role role);

}
