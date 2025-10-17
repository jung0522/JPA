package io.goorm.jpa.controller;

import io.goorm.jpa.dto.PostCreateRequest;
import io.goorm.jpa.dto.PostResponse;
import io.goorm.jpa.entity.Post;
import io.goorm.jpa.repository.PostRepository;
import io.goorm.jpa.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post API", description = "게시글 API - N+1 문제 해결 방법 학습")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    /**
     * 1. N+1 문제 발생
     */
    @Operation(summary = "1. N+1 문제 발생", description = "LAZY 로딩으로 인한 N+1 문제 시연 (1 + N번 쿼리)")
    @GetMapping("/n-plus-1")
    public List<PostResponse> getPostsWithNPlusOne() {
        return postService.getPostsWithNPlusOne();
    }

    /**
     * 2. 일반 JOIN (N+1 여전히 발생 - 비교용)
     */
    @Operation(summary = "2. 일반 JOIN", description = "JOIN 명시했지만 FETCH 없어서 N+1 여전히 발생 (4번 쿼리)")
    @GetMapping("/join")
    public List<PostResponse> getPostsWithJoin() {
        return postService.getPostsWithJoin();
    }

    /**
     * 3. Fetch Join으로 해결
     */
    @Operation(summary = "3. FETCH JOIN", description = "FETCH JOIN으로 N+1 해결 (1번 쿼리)")
    @GetMapping("/fetch-join")
    public List<PostResponse> getPostsWithFetchJoin() {
        return postService.getPostsWithFetchJoin();
    }

    /**
     * 4. @EntityGraph로 해결
     */
    @Operation(summary = "4. @EntityGraph", description = "@EntityGraph로 N+1 해결 (1번 쿼리, 어노테이션만)")
    @GetMapping("/entity-graph")
    public List<PostResponse> getPostsWithEntityGraph() {
        return postService.getPostsWithEntityGraph();
    }

    /**
     * 5. @BatchSize로 해결
     */
    @Operation(summary = "5. @BatchSize", description = "@BatchSize로 N+1 최소화 (2번 쿼리, IN 절 사용)")
    @GetMapping("/batch-size")
    public List<PostResponse> getPostsWithBatchSize() {
        return postService.getPostsWithBatchSize();
    }

    /**
     * 6. Entity 직접 반환 (문제 발생)
     */
    @Operation(summary = "6. Entity 직접 반환 (권장하지 않음)", description = "Entity 리스트 반환 - 보안/설계상 DTO 권장")
    @GetMapping("/entitys")
    public List<Post>  getPostEntity(@PathVariable Long id)  { return postService.getEntity(); }

    /**
     * 7. DTO 반환 (정상)
     */
    @Operation(summary = "7. DTO 반환 (권장)", description = "DTO로 변환하여 안전하게 반환")
    @GetMapping("/{id}/dto")
    public PostResponse getPostDto(@PathVariable Long id) {
        return postService.getPost(id);
    }

    /**
     * 8. 게시글 목록 (페이징)
     */
    @Operation(summary = "8. 페이징 조회", description = "Spring Data JPA Pageable로 페이징 처리 (page, size, sort)")
    @GetMapping
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postService.getPosts(pageable);
    }

    /**
     * 9. 게시글 생성 (Cascade.PERSIST)
     */
    @Operation(summary = "9. 게시글 생성 (Cascade.PERSIST)", description = "게시글 저장 시 댓글도 자동 저장")
    @PostMapping
    public PostResponse createPost(@RequestBody PostCreateRequest request) {
        return postService.createPost(request);
    }

    /**
     * 10. 게시글 삭제 (Cascade.REMOVE)
     */
    @Operation(summary = "10. 게시글 삭제 (Cascade.REMOVE)", description = "게시글 삭제 시 댓글도 자동 삭제")
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "게시글과 댓글이 모두 삭제되었습니다.";
    }

    /**
     * 11. 댓글 삭제 (orphanRemoval)
     */
    @Operation(summary = "11. 댓글 삭제 (orphanRemoval)", description = "Post에서 관계를 끊으면 Comment 자동 삭제")
    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id) {
        postService.deleteComment(id);
        return "댓글이 삭제되었습니다.";
    }
}
