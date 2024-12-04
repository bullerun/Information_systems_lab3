package com.example.information_systems_lab1.authentication.responses;

import com.example.information_systems_lab1.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private Long id;
    private String username;
    private String token;
    private Role role;
}
