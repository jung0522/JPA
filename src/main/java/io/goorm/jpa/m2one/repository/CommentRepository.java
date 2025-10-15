package io.goorm.jpa.m2one.repository;

import io.goorm.jpa.m2one.dto.CommentWithPostDto;
import io.goorm.jpa.m2one.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Fetch Join (N+1 해결)
    @Query("select c from Comment c join fetch c.post")
    List<Comment> findAllWithPost();

    // DTO 프로젝션 (페이징 가능)
    @Query("""
        select new io.goorm.jpa.m2one.dto.CommentWithPostDto(
            c.id, c.content, p.id, p.title
        )
        from Comment c
        join c.post p
        """)
    Page<CommentWithPostDto> findCommentsWithPost(Pageable pageable);

    // 게시글별 댓글 개수
    @Query("select count(c) from Comment c where c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // 게시글별 댓글 목록(페이징)
    @Query("select c from Comment c where c.post.id = :postId order by c.id desc")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);
}
