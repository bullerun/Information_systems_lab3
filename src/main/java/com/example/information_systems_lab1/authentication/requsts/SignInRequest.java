package com.example.information_systems_lab1.authentication.requsts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignInRequest {
    @NotNull(message = "username не должен быть пустым")
    private String username;
    @NotNull(message = "password не должен быть пустым")
    private String password;
}
