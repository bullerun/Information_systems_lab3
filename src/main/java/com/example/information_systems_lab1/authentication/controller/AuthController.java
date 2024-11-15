package com.example.information_systems_lab1.authentication.controller;

import com.example.information_systems_lab1.authentication.requsts.SignInRequest;
import com.example.information_systems_lab1.authentication.requsts.SignUpRequest;
import com.example.information_systems_lab1.authentication.responses.JwtAuthenticationResponse;
import com.example.information_systems_lab1.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public JwtAuthenticationResponse signUp(@Valid @RequestBody SignUpRequest sign) {
        return authenticationService.signUp(sign);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@Valid @RequestBody SignInRequest sign) {
        return authenticationService.signIn(sign);

    }
}