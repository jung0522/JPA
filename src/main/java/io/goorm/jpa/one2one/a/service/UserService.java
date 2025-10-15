package io.goorm.jpa.one2one.a.service;

import io.goorm.jpa.one2one.a.entity.User;
import io.goorm.jpa.one2one.a.entity.UserProfile;
import io.goorm.jpa.one2one.a.repository.UserProfileRepository;
import io.goorm.jpa.one2one.a.repository.UserRepository;
import io.goorm.jpa.one2one.dto.UserCreateRequest;
import io.goorm.jpa.one2one.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // A안: 2번 저장 과정
        // 1. User 먼저 저장
        User user = new User(request.username(), request.email());
        user = userRepository.save(user);
        
        // 2. UserProfile에 User 세팅 후 저장
        UserProfile profile = new UserProfile(request.fullName(), request.phone(), request.address());
        profile.setUser(user);
        userProfileRepository.save(profile);
        
        return UserResponse.from(user);
    }
    
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.from(user);
    }
    
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }
}
