package io.goorm.jpa.m2m.repository;

import io.goorm.jpa.m2m.dto.PostWithTagsDto;
import io.goorm.jpa.m2m.entity.Post;
import io.goorm.jpa.m2m.entity.PostTag;
import io.goorm.jpa.m2m.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    
    // R-1: 게시글 상세 + 태그 목록
    @Query("""
        SELECT t FROM PostTag pt 
        JOIN pt.tag t 
        WHERE pt.post.id = :postId 
        ORDER BY pt.order ASC
        """)
    List<Tag> findTagsByPostId(@Param("postId") Long postId);
    
    // R-2: 태그 상세 + 게시글 목록(페이징)
    @Query("""
        SELECT p FROM PostTag pt 
        JOIN pt.post p 
        WHERE pt.tag.id = :tagId
        """)
    Page<Post> findPostsByTagId(@Param("tagId") Long tagId, Pageable pageable);
    
    // R-5: 태그명으로 게시글 검색(페이징)
    @Query("""
        SELECT p FROM PostTag pt 
        JOIN pt.post p 
        JOIN pt.tag t 
        WHERE t.name = :tagName
        """)
    Page<Post> findPostsByTagName(@Param("tagName") String tagName, Pageable pageable);
    
    // R-4: 태그별 게시글 수 집계
    @Query("""
        SELECT t.id, t.name, COUNT(pt) 
        FROM PostTag pt 
        JOIN pt.tag t 
        GROUP BY t.id, t.name 
        ORDER BY COUNT(pt) DESC
        """)
    List<Object[]> findTagCounts();
    
    // CUD 관련
    Optional<PostTag> findByPostIdAndTagId(Long postId, Long tagId);
    
    void deleteByPostIdAndTagId(Long postId, Long tagId);
    
    @Query("""
        SELECT pt FROM PostTag pt 
        WHERE pt.post.id = :postId 
        ORDER BY pt.order ASC
        """)
    List<PostTag> findByPostIdOrderByOrderAsc(@Param("postId") Long postId);
    
    @Query("""
        SELECT pt FROM PostTag pt 
        WHERE pt.tag.id = :tagId
        """)
    List<PostTag> findByTagId(@Param("tagId") Long tagId);
    
    // Alt) EntityGraph 버전 (주석 예시)
    // @EntityGraph(attributePaths = {"tag"})
    // List<PostTag> findByPostIdOrderByOrderAsc(Long postId);
    
    // Alt) fetch join 버전
    @Query("""
        SELECT pt FROM PostTag pt 
        JOIN FETCH pt.tag t 
        WHERE pt.post.id = :postId 
        ORDER BY pt.order ASC
        """)
    List<PostTag> findPostTagsWithTagByPostId(@Param("postId") Long postId);
    
    // R-3: 게시글 목록 + 각 게시글의 태그들(프리뷰) - DTO 프로젝션
    @Query("""
        SELECT new io.goorm.jpa.m2m.dto.PostWithTagsDto(
            p.id,
            p.title,
            GROUP_CONCAT(t.name)
        )
        FROM Post p
        LEFT JOIN PostTag pt ON pt.post = p
        LEFT JOIN Tag t ON t = pt.tag
        WHERE (:keyword IS NULL OR p.title LIKE CONCAT(:keyword, '%'))
        GROUP BY p.id, p.title
        """)
    Page<PostWithTagsDto> findPostsWithTags(@Param("keyword") String keyword, Pageable pageable);
}
