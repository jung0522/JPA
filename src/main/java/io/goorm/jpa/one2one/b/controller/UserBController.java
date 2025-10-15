package io.goorm.jpa.one2one.b.controller;

import io.goorm.jpa.one2one.b.service.UserService;
import io.goorm.jpa.one2one.dto.UserCreateRequest;
import io.goorm.jpa.one2one.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/one2one/b")
@RequiredArgsConstructor
public class UserBController {
    
    @Qualifier("userBService")
    private final UserService userService;
    
    // C-1: 사용자 생성 (1번 저장 - Cascade)
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
    
    // R-2: 사용자 목록 조회 (N+1 발생)
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> responses = userService.getUsers();
        return ResponseEntity.ok(responses);
    }
    
    // R-3: Fetch Join으로 N+1 해결
    @GetMapping("/users/with-fetch")
    public ResponseEntity<List<UserResponse>> getUsersWithProfile() {
        List<UserResponse> responses = userService.getUsersWithProfile();
        return ResponseEntity.ok(responses);
    }
}
