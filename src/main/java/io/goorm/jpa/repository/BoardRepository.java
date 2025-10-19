package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Board;
import io.goorm.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Board Repository
 * Query Methods 사용
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 제목으로 검색 (페이징)
     */
    Page<Board> findByTitleContainingAndDeletedFalse(String keyword, Pageable pageable);

    /**
     * 작성자별 조회 (페이징)
     */
    Page<Board> findByAuthorAndDeletedFalse(User author, Pageable pageable);

    /**
     * 전체 목록 조회 (삭제 안된 것만, 페이징)
     */
    Page<Board> findByDeletedFalse(Pageable pageable);

    /**
     * 인기 게시글 Top 10 (조회수 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.viewCount DESC")
    List<Board> findTop10ByOrderByViewCountDesc(Pageable pageable);

    /**
     * 삭제되지 않은 게시글 수
     */
    Long countByDeletedFalse();
}
