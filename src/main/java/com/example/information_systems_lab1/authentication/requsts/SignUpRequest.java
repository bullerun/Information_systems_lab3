package com.example.information_systems_lab1.authentication.requsts;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
}
