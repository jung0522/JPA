package io.goorm.jpa.one2one.dto;

public record UserProfileResponse(
    Long id,
    String fullName,
    String phone,
    String address
) {
    public static UserProfileResponse from(Object profile) {
        if (profile == null) {
            return null;
        }
        
        // A안: UserProfile 엔티티
        if (profile.getClass().getSimpleName().equals("UserProfile")) {
            try {
                var id = (Long) profile.getClass().getMethod("getId").invoke(profile);
                var fullName = (String) profile.getClass().getMethod("getFullName").invoke(profile);
                var phone = (String) profile.getClass().getMethod("getPhone").invoke(profile);
                var address = (String) profile.getClass().getMethod("getAddress").invoke(profile);
                
                return new UserProfileResponse(id, fullName, phone, address);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create UserProfileResponse", e);
            }
        }
        
        throw new IllegalArgumentException("Unsupported profile type: " + profile.getClass());
    }
}
