package com.uptimemonitor.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String email
) {
}
