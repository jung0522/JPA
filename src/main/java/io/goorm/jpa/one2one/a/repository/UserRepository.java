package io.goorm.jpa.one2one.a.repository;

import io.goorm.jpa.one2one.a.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
