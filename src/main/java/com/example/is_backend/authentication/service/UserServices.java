package com.example.is_backend.authentication.service;

import com.example.is_backend.entity.Role;
import com.example.is_backend.entity.User;
import com.example.is_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        return save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }


    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public void setAdmin(Long id) {
        repository.updateRole(id, Role.ROLE_ADMIN);
    }

    public void removeAdmin(Long id) {
        repository.updateRole(id, Role.ROLE_USER);
    }
}