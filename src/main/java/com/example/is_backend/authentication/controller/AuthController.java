package com.example.is_backend.authentication.controller;

import com.example.is_backend.authentication.requsts.SignInRequest;
import com.example.is_backend.authentication.requsts.SignUpRequest;
import com.example.is_backend.authentication.responses.JwtAuthenticationResponse;
import com.example.is_backend.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public JwtAuthenticationResponse signUp(@Valid @RequestBody SignUpRequest sign) throws IllegalArgumentException {
        return authenticationService.signUp(sign);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(@Valid @RequestBody SignInRequest sign) {
        return authenticationService.signIn(sign);
    }

}