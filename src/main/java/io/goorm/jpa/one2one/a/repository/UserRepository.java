package io.goorm.jpa.one2one.a.repository;

import io.goorm.jpa.one2one.a.entity.UserA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userARepository")
public interface UserRepository extends JpaRepository<UserA, Long> {
    
    // A안에서는 단순 조회만 (UserA는 UserProfileA를 참조하지 않음)
    // @Query("SELECT u FROM UserA u LEFT JOIN FETCH UserProfileA p ON p.user = u")
    // List<UserA> findAllWithProfile();
}
