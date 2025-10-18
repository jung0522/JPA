package io.goorm.jpa.dto.profile;

import jakarta.validation.constraints.Size;

/**
 * 프로필 생성/수정 요청 DTO
 */
public record UserProfileRequest(
        @Size(max = 20, message = "{profile.phone.size}")
        String phone,

        @Size(max = 200, message = "{profile.address.size}")
        String address,

        @Size(max = 500, message = "{profile.bio.size}")
        String bio
) {
}
