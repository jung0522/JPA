package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * JpaRepository에서 기본 제공하는 메서드들 (오버라이드 불필요)
     *
     * - List<Post> findAll()                           // 전체 조회
     * - Page<Post> findAll(Pageable pageable)          // 페이징 조회
     * - Optional<Post> findById(Long id)               // ID로 조회
     * - Post save(Post post)                           // 저장/수정
     * - void delete(Post post)                         // 삭제
     * - void deleteById(Long id)                       // ID로 삭제
     * - long count()                                   // 전체 개수
     * - boolean existsById(Long id)                    // 존재 여부
     */

    // 일반 JOIN (N+1 여전히 발생 - 비교용)
    @Query("SELECT p FROM Post p LEFT JOIN p.comments")
    List<Post> findAllWithJoin();

    // Fetch Join으로 해결
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments")
    List<Post> findAllWithFetchJoin();

    // @EntityGraph로 해결
    @EntityGraph(attributePaths = "comments")
    @Query("SELECT p FROM Post p")
    List<Post> findAllWithEntityGraph();
}
