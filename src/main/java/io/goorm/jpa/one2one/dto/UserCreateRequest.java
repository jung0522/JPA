package io.goorm.jpa.one2one.dto;

public record UserCreateRequest(
    String username,
    String email,
    String fullName,
    String phone,
    String address
) {
}
