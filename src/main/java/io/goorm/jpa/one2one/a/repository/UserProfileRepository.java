package io.goorm.jpa.one2one.a.repository;

import io.goorm.jpa.one2one.a.entity.UserProfileA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userProfileARepository")
public interface UserProfileRepository extends JpaRepository<UserProfileA, Long> {
}
