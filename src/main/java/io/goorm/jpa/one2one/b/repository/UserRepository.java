package io.goorm.jpa.one2one.b.repository;

import io.goorm.jpa.one2one.b.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
