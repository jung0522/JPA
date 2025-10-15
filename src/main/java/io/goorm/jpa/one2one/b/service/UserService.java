package io.goorm.jpa.one2one.b.service;

import io.goorm.jpa.one2one.b.entity.UserB;
import io.goorm.jpa.one2one.b.entity.UserProfileB;
import io.goorm.jpa.one2one.b.repository.UserRepository;
import io.goorm.jpa.one2one.dto.UserCreateRequest;
import io.goorm.jpa.one2one.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userBService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    @Qualifier("userBRepository")
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // B안: 1번 저장 (Cascade)
        UserB user = new UserB(request.username(), request.email());
        UserProfileB profile = new UserProfileB(request.fullName(), request.phone(), request.address());
        user.setProfile(profile);
        
        // Cascade로 profile INSERT까지 처리
        user = userRepository.save(user);
        
        return UserResponse.from(user);
    }
    
    public UserResponse getUser(Long id) {
        UserB user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.from(user);
    }
    
    public List<UserResponse> getUsers() {
        List<UserB> users = userRepository.findAll();
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }
    
    // Fetch Join으로 N+1 해결
    public List<UserResponse> getUsersWithProfile() {
        List<UserB> users = userRepository.findAllWithProfile();
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }
}
