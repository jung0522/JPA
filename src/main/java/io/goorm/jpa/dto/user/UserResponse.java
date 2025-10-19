package io.goorm.jpa.dto.user;

import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;

/**
 * 사용자 응답 DTO
 */
public record UserResponse(
    Long userNo,
    String username,
    String fullName,
    String email,
    UserRole role,
    String phone,
    String address,
    String bio
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getUserNo(),
            user.getUsername(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.getPhone(),
            user.getAddress(),
            user.getBio()
        );
    }
}
