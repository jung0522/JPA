package io.goorm.jpa.one2one.a.controller;

import io.goorm.jpa.one2one.a.service.UserService;
import io.goorm.jpa.one2one.dto.UserCreateRequest;
import io.goorm.jpa.one2one.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/one2one/a")
@RequiredArgsConstructor
public class UserAController {
    
    private final UserService userService;
    
    // C-1: 사용자 생성 (2번 저장)
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }
    
    // R-1: 사용자 상세 조회 (EAGER vs LAZY 실험)
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(response);
    }
    
    // R-2: 사용자 목록 조회 (N+1 확인)
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> responses = userService.getUsers();
        return ResponseEntity.ok(responses);
    }
}
