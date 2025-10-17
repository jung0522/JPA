package io.goorm.jpa.m2m.repository;

import io.goorm.jpa.m2m.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    // 특정 게시글의 모든 태그 조회
    List<PostTag> findByPostId(Long postId);

    // 특정 태그가 사용된 모든 게시글 조회
    List<PostTag> findByTagId(Long tagId);

    // 중복 체크: 이미 해당 게시글에 태그가 있는지 확인
    boolean existsByPostIdAndTagId(Long postId, Long tagId);

    // 특정 게시글의 특정 태그 삭제
    void deleteByPostIdAndTagId(Long postId, Long tagId);
}
