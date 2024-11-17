package com.example.information_systems_lab1.authentication.service;

import com.example.information_systems_lab1.authentication.requsts.SignInRequest;
import com.example.information_systems_lab1.authentication.requsts.SignUpRequest;
import com.example.information_systems_lab1.entity.User;

import com.example.information_systems_lab1.authentication.responses.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserServices userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) throws IllegalArgumentException {

        // Создаем нового пользователя
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userService.create(user);

        // Генерируем токен
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        // Аутентификация пользователя
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // Загружаем пользователя
        var userDetails = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        // Генерируем токен
        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
