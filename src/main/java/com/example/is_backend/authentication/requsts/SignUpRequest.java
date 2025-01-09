package com.example.is_backend.authentication.requsts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotNull(message = "username не должен быть пустым")
    private String username;
    @NotNull(message = "password не должен быть пустым")
    private String password;
}
