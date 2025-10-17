package io.goorm.jpa.m2m.repository;

import io.goorm.jpa.m2m.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    // 태그 이름으로 조회 (중복 체크용)
    Optional<Tag> findByName(String name);

    // 태그 이름 존재 여부 확인
    boolean existsByName(String name);
}
