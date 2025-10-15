package io.goorm.jpa.one2one.b.repository;

import io.goorm.jpa.one2one.b.entity.UserB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userBRepository")
public interface UserRepository extends JpaRepository<UserB, Long> {
    
    // Fetch Join 메소드 (B안에서는 UserB가 UserProfileB를 참조)
    @Query("SELECT u FROM UserB u JOIN FETCH u.profile")
    List<UserB> findAllWithProfile();
}
