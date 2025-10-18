package io.goorm.jpa.repository;

import io.goorm.jpa.entity.User;
import io.goorm.jpa.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * UserProfile Repository
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * 사용자로 프로필 조회 (Fetch Join)
     */
    @Query("SELECT p FROM UserProfile p JOIN FETCH p.user WHERE p.user = :user AND p.deleted = false")
    Optional<UserProfile> findByUser(@Param("user") User user);

    /**
     * 프로필 존재 여부
     */
    boolean existsByUserAndDeletedFalse(User user);
}
