package io.goorm.jpa.one2one.a.service;

import io.goorm.jpa.one2one.a.entity.UserA;
import io.goorm.jpa.one2one.a.entity.UserProfileA;
import io.goorm.jpa.one2one.a.repository.UserProfileRepository;
import io.goorm.jpa.one2one.a.repository.UserRepository;
import io.goorm.jpa.one2one.dto.UserCreateRequest;
import io.goorm.jpa.one2one.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userAService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    @Qualifier("userARepository")
    private final UserRepository userRepository;
    @Qualifier("userProfileARepository")
    private final UserProfileRepository userProfileRepository;
    
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // A안: 2번 저장 과정
        // 1. UserA 먼저 저장
        UserA user = new UserA(request.username(), request.email());
        user = userRepository.save(user);
        
        // 2. UserProfileA에 UserA 세팅 후 저장
        UserProfileA profile = new UserProfileA(request.fullName(), request.phone(), request.address());
        profile.setUser(user);
        userProfileRepository.save(profile);
        
        return UserResponse.from(user);
    }
    
    public UserResponse getUser(Long id) {
        UserA user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.from(user);
    }
    
    public List<UserResponse> getUsers() {
        // A안: 단순 조회 (UserA는 UserProfileA를 참조하지 않음)
        List<UserA> users = userRepository.findAll();
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }
}
