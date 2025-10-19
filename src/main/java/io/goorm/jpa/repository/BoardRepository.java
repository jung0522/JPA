package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Board;
import io.goorm.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Board Repository
 * Query Methods 사용 - 다양한 검색 조건 지원
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    // ===== 기본 검색 =====
    
    /**
     * 제목으로 검색 (페이징)
     */
    Page<Board> findByTitleContainingAndDeletedFalse(String keyword, Pageable pageable);

    /**
     * 내용으로 검색 (페이징)
     */
    Page<Board> findByContentContainingAndDeletedFalse(String keyword, Pageable pageable);

    /**
     * 제목 또는 내용으로 검색 (페이징)
     */
    Page<Board> findByTitleContainingOrContentContainingAndDeletedFalse(String titleKeyword, String contentKeyword, Pageable pageable);

    /**
     * 작성자별 조회 (페이징)
     */
    Page<Board> findByAuthorAndDeletedFalse(User author, Pageable pageable);

    /**
     * 작성자명으로 검색 (페이징)
     */
    Page<Board> findByAuthorFullNameContainingAndDeletedFalse(String authorName, Pageable pageable);

    /**
     * 전체 목록 조회 (삭제 안된 것만, 페이징)
     */
    Page<Board> findByDeletedFalse(Pageable pageable);

    // ===== 날짜 범위 검색 =====

    /**
     * 특정 날짜 이후 작성된 게시글
     */
    Page<Board> findByCreatedAtAfterAndDeletedFalse(LocalDateTime date, Pageable pageable);

    /**
     * 특정 날짜 이전 작성된 게시글
     */
    Page<Board> findByCreatedAtBeforeAndDeletedFalse(LocalDateTime date, Pageable pageable);

    /**
     * 날짜 범위로 검색
     */
    Page<Board> findByCreatedAtBetweenAndDeletedFalse(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // ===== 조회수 기반 검색 =====

    /**
     * 조회수 이상인 게시글
     */
    Page<Board> findByViewCountGreaterThanEqualAndDeletedFalse(Integer minViewCount, Pageable pageable);

    /**
     * 조회수 이하인 게시글
     */
    Page<Board> findByViewCountLessThanEqualAndDeletedFalse(Integer maxViewCount, Pageable pageable);

    /**
     * 조회수 범위로 검색
     */
    Page<Board> findByViewCountBetweenAndDeletedFalse(Integer minViewCount, Integer maxViewCount, Pageable pageable);

    // ===== 복합 검색 =====

    /**
     * 제목 + 작성자로 검색
     */
    Page<Board> findByTitleContainingAndAuthorAndDeletedFalse(String titleKeyword, User author, Pageable pageable);

    /**
     * 제목 + 조회수 이상으로 검색
     */
    Page<Board> findByTitleContainingAndViewCountGreaterThanEqualAndDeletedFalse(String titleKeyword, Integer minViewCount, Pageable pageable);

    /**
     * 작성자 + 조회수 이상으로 검색
     */
    Page<Board> findByAuthorAndViewCountGreaterThanEqualAndDeletedFalse(User author, Integer minViewCount, Pageable pageable);

    /**
     * 제목 + 날짜 범위로 검색
     */
    Page<Board> findByTitleContainingAndCreatedAtBetweenAndDeletedFalse(String titleKeyword, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 제목, 내용, 작성자명으로 통합 검색 (각각 선택적)
     */
    @Query("""
        SELECT b FROM Board b 
        WHERE b.deleted = false
        AND (:title IS NULL OR :title = '' OR b.title LIKE %:title%)
        AND (:content IS NULL OR :content = '' OR b.content LIKE %:content%)
        AND (:author IS NULL OR :author = '' OR b.author.fullName LIKE %:author%)
        """)
    Page<Board> searchByTitleAndContentAndAuthor(
        @Param("title") String title,
        @Param("content") String content, 
        @Param("author") String author,
        Pageable pageable
    );

    // ===== 정렬된 조회 =====

    /**
     * 조회수 내림차순 정렬
     */
    Page<Board> findByDeletedFalseOrderByViewCountDesc(Pageable pageable);

    /**
     * 조회수 오름차순 정렬
     */
    Page<Board> findByDeletedFalseOrderByViewCountAsc(Pageable pageable);

    /**
     * 제목으로 검색 + 조회수 내림차순
     */
    Page<Board> findByTitleContainingAndDeletedFalseOrderByViewCountDesc(String keyword, Pageable pageable);

    /**
     * 작성자별 + 조회수 내림차순
     */
    Page<Board> findByAuthorAndDeletedFalseOrderByViewCountDesc(User author, Pageable pageable);

    // ===== 통계 및 집계 =====

    /**
     * 인기 게시글 Top N (조회수 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.viewCount DESC")
    List<Board> findTopByOrderByViewCountDesc(Pageable pageable);

    /**
     * 인기 게시글 Top 10 (조회수 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.viewCount DESC")
    List<Board> findTop10ByOrderByViewCountDesc(Pageable pageable);

    /**
     * 최신 게시글 Top N (생성일 내림차순)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.createdAt DESC")
    List<Board> findTopByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 특정 작성자의 게시글 수
     */
    Long countByAuthorAndDeletedFalse(User author);

    /**
     * 조회수 이상인 게시글 수
     */
    Long countByViewCountGreaterThanEqualAndDeletedFalse(Integer minViewCount);

    /**
     * 특정 기간 내 게시글 수
     */
    Long countByCreatedAtBetweenAndDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 삭제되지 않은 게시글 수
     */
    Long countByDeletedFalse();

    // ===== 고급 검색 (JPQL) =====

    /**
     * 제목과 내용을 동시에 검색 (OR 조건)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 제목, 내용, 작성자명을 동시에 검색
     */
    @Query("SELECT b FROM Board b JOIN b.author a WHERE b.deleted = false AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR a.fullName LIKE %:keyword%)")
    Page<Board> searchByTitleOrContentOrAuthor(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 게시글 (조회수 기준 상위 N개)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.viewCount DESC")
    Page<Board> findPopularBoards(Pageable pageable);

    /**
     * 최근 게시글 (생성일 기준 최근 N개)
     */
    @Query("SELECT b FROM Board b WHERE b.deleted = false ORDER BY b.createdAt DESC")
    Page<Board> findRecentBoards(Pageable pageable);
}
