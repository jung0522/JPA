package io.goorm.jpa.one2one.dto;

public record UserResponse(
    Long id,
    String username,
    String email,
    UserProfileResponse profile
) {
    public static UserResponse from(Object user) {
        try {
            var id = (Long) user.getClass().getMethod("getId").invoke(user);
            var username = (String) user.getClass().getMethod("getUsername").invoke(user);
            var email = (String) user.getClass().getMethod("getEmail").invoke(user);
            
            // 프로필 정보 가져오기
            UserProfileResponse profile = null;
            try {
                var profileObj = user.getClass().getMethod("getProfile").invoke(user);
                profile = UserProfileResponse.from(profileObj);
            } catch (Exception e) {
                // A안의 경우 UserProfile이 별도로 관리됨
                // B안의 경우 User에 profile이 있음
            }
            
            return new UserResponse(id, username, email, profile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create UserResponse", e);
        }
    }
}
