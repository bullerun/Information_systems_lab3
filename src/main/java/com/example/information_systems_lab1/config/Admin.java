package com.example.information_systems_lab1.config;

import com.example.information_systems_lab1.entity.Role;
import com.example.information_systems_lab1.entity.User;
import com.example.information_systems_lab1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Admin {

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UserRepository userRepository; // Заменено на final
    private final PasswordEncoder passwordEncoder; // Заменено на final

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            var user = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(user);
        }
    }

}
