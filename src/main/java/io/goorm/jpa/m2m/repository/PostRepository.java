package io.goorm.jpa.m2m.repository;

import io.goorm.jpa.m2m.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 기본 CRUD 메서드만 사용
    // findAll(), findById(), save(), delete() 등
}
