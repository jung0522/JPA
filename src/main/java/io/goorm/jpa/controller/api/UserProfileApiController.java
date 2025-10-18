package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.profile.UserProfileRequest;
import io.goorm.jpa.dto.profile.UserProfileResponse;
import io.goorm.jpa.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * UserProfile API Controller
 * OneToOne 단방향
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "UserProfile", description = "사용자 프로필 API")
public class UserProfileApiController {

    private final UserProfileService userProfileService;

    @GetMapping
    @Operation(summary = "내 프로필 조회")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        UserProfileResponse response = userProfileService.getMyProfile();
        return ApiResponse.ok(response);
    }

    @PostMapping
    @Operation(summary = "프로필 생성")
    public ApiResponse<UserProfileResponse> createProfile(@Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.createProfile(request);
        return ApiResponse.ok(response);
    }

    @PutMapping
    @Operation(summary = "프로필 수정")
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse response = userProfileService.updateProfile(request);
        return ApiResponse.ok(response);
    }
}
