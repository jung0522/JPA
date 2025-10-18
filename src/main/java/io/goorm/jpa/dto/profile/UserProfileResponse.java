package io.goorm.jpa.dto.profile;

import io.goorm.jpa.entity.UserProfile;

/**
 * 프로필 응답 DTO
 */
public record UserProfileResponse(
        Long profileNo,
        Long userNo,
        String username,
        String fullName,
        String email,
        String phone,
        String address,
        String bio
) {
    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(
                profile.getProfileNo(),
                profile.getUser().getUserNo(),
                profile.getUser().getUsername(),
                profile.getUser().getFullName(),
                profile.getUser().getEmail(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getBio()
        );
    }
}
