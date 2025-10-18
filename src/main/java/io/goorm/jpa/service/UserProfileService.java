package io.goorm.jpa.service;

import io.goorm.jpa.dto.profile.UserProfileRequest;
import io.goorm.jpa.dto.profile.UserProfileResponse;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.entity.UserProfile;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.UserProfileRepository;
import io.goorm.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserProfile Service
 * OneToOne 단방향
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    /**
     * 내 프로필 조회
     */
    public UserProfileResponse getMyProfile() {
        User currentUser = getCurrentUser();

        UserProfile profile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return UserProfileResponse.from(profile);
    }

    /**
     * 프로필 생성
     */
    @Transactional
    public UserProfileResponse createProfile(UserProfileRequest request) {
        User currentUser = getCurrentUser();

        // 중복 확인
        if (userProfileRepository.existsByUserAndDeletedFalse(currentUser)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        UserProfile profile = UserProfile.builder()
                .user(currentUser)
                .phone(request.phone())
                .address(request.address())
                .bio(request.bio())
                .build();

        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("Profile created: profileNo={}, user={}", savedProfile.getProfileNo(), currentUser.getUsername());

        return UserProfileResponse.from(savedProfile);
    }

    /**
     * 프로필 수정
     */
    @Transactional
    public UserProfileResponse updateProfile(UserProfileRequest request) {
        User currentUser = getCurrentUser();

        UserProfile profile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        profile.update(request.phone(), request.address(), request.bio());
        log.info("Profile updated: profileNo={}, user={}", profile.getProfileNo(), currentUser.getUsername());

        return UserProfileResponse.from(profile);
    }

    /**
     * 현재 로그인한 사용자 조회
     */
    private User getCurrentUser() {
        String userNo = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
