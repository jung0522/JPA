package io.goorm.jpa.dto.auth;

public record LoginResponse(
        String token,
        Long userId,
        String username,
        String role
) {
}
