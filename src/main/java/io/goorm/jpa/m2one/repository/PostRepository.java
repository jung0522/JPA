package io.goorm.jpa.m2one.repository;

import io.goorm.jpa.m2one.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
