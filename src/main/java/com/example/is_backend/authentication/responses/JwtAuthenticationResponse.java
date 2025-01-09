package com.example.is_backend.authentication.responses;

import com.example.is_backend.entity.Role;
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
