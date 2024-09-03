package org.example.cloudfilestorage.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.example.cloudfilestorage.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;

    private String userName;

    @Getter
    private String email;

    @JsonIgnore
    private String password;

    @Getter
    private Boolean accountNonExpired;
    @Getter
    private Boolean accountNonLocked;
    @Getter
    private Boolean credentialsNonExpired;
    @Getter
    private Boolean enabled;

    // Коллекция authorities хранит права доступа пользователя
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities, Boolean accountNonExpired, Boolean accountNonLocked,
                           Boolean credentialsNonExpired, Boolean enabled) {
        this.id = id;
        this.userName = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                true,
                true,
                true,
                user.getEnabled());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    // Реализация метода isAccountNonExpired из интерфейса UserDetails
    // Возвращает true, если учетная запись не просрочена
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    // Реализация метода isAccountNonLocked из интерфейса UserDetails
    // Возвращает true, если учетная запись не заблокирована
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    // Реализация метода isCredentialsNonExpired из интерфейса UserDetails
    // Возвращает true, если учетные данные не просрочены
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    // Реализация метода isEnabled из интерфейса UserDetails
    // Возвращает true, если учетная запись включена
    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
