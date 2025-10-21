package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.auth.LoginRequest;
import io.goorm.jpa.dto.auth.LoginResponse;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.repository.UserRepository;
import io.goorm.jpa.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "세션 기반 인증 - 자동으로 쿠키 설정")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request);

        // SecurityContext를 세션에 명시적으로 저장
        SecurityContext context = SecurityContextHolder.getContext();
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "세션 무효화 및 인증 정보 삭제")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "로그인된 사용자의 기본 정보 반환")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        Long userNo = (Long) authentication.getPrincipal();
        User user = userRepository.findById(userNo).orElse(null);

        if (user == null) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userNo", user.getUserNo());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
