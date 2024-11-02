package com.example.information_systems_lab1.authentication.requsts;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
