package io.goorm.jpa.one2one.b.service;

import io.goorm.jpa.one2one.b.entity.User;
import io.goorm.jpa.one2one.b.entity.UserProfile;
import io.goorm.jpa.one2one.b.repository.UserRepository;
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
    
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // B안: 1번 저장 (Cascade)
        User user = new User(request.username(), request.email());
        UserProfile profile = new UserProfile(request.fullName(), request.phone(), request.address());
        user.setProfile(profile);
        
        // Cascade로 profile INSERT까지 처리
        user = userRepository.save(user);
        
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
