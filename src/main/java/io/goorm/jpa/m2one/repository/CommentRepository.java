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

    // 일반 Join (N+1 발생 - WHERE 조건용으로만 사용)
    // JOIN: SELECT 절에 post가 포함되지 않음 → post 사용 시 지연 로딩으로 추가 쿼리 발생
    @Query("select c from Comment c join c.post p")
    List<Comment> findAllWithJoin();

    // Fetch Join (N+1 해결 - 즉시 로딩)
    // FETCH JOIN: SELECT 절에 post도 함께 조회 → 한 번의 쿼리로 모두 가져옴
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
