package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.auth.LoginRequest;
import io.goorm.jpa.dto.auth.LoginResponse;
import io.goorm.jpa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
