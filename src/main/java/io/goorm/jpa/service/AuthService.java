package io.goorm.jpa.service;

import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.config.jwt.JwtTokenProvider;
import io.goorm.jpa.dto.auth.LoginRequest;
import io.goorm.jpa.dto.auth.LoginResponse;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(
                user.getUserNo().toString(),
                user.getRole().name()
        );

        log.info("User logged in: username={}, role={}", user.getUsername(), user.getRole());

        return new LoginResponse(
                token,
                user.getUserNo(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
