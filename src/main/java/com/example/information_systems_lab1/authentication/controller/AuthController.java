package com.example.information_systems_lab1.authentication.controller;

import com.example.information_systems_lab1.authentication.requsts.SignInRequest;
import com.example.information_systems_lab1.authentication.requsts.SignUpRequest;
import com.example.information_systems_lab1.authentication.responses.JwtAuthenticationResponse;
import com.example.information_systems_lab1.authentication.service.AuthenticationService;
import com.example.information_systems_lab1.authentication.service.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final UserServices service;

    @PostMapping("/register")
    public JwtAuthenticationResponse signUp(@Valid @RequestBody SignUpRequest sign) throws IllegalArgumentException {
        return authenticationService.signUp(sign);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@Valid @RequestBody SignInRequest sign) {
        return authenticationService.signIn(sign);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String exampleAdmin() {
        return "Hello, admin!";
    }

    @GetMapping("/set-admin")
    public void setAdmin() {
        service.setAdmin();
    }
}