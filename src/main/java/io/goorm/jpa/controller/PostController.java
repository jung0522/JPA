package io.goorm.jpa.controller;

import io.goorm.jpa.dto.PostCreateRequest;
import io.goorm.jpa.dto.PostResponse;
import io.goorm.jpa.entity.Post;
import io.goorm.jpa.repository.PostRepository;
import io.goorm.jpa.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    /**
     * 1. N+1 문제 발생
     */
    @GetMapping("/n-plus-1")
    public List<PostResponse> getPostsWithNPlusOne() {
        return postService.getPostsWithNPlusOne();
    }

    /**
     * 2. 일반 JOIN (N+1 여전히 발생 - 비교용)
     */
    @GetMapping("/join")
    public List<PostResponse> getPostsWithJoin() {
        return postService.getPostsWithJoin();
    }

    /**
     * 3. Fetch Join으로 해결
     */
    @GetMapping("/fetch-join")
    public List<PostResponse> getPostsWithFetchJoin() {
        return postService.getPostsWithFetchJoin();
    }

    /**
     * 4. @EntityGraph로 해결
     */
    @GetMapping("/entity-graph")
    public List<PostResponse> getPostsWithEntityGraph() {
        return postService.getPostsWithEntityGraph();
    }

    /**
     * 5. @BatchSize로 해결
     */
    @GetMapping("/batch-size")
    public List<PostResponse> getPostsWithBatchSize() {
        return postService.getPostsWithBatchSize();
    }

    /**
     * 6. Entity 직접 반환 (문제 발생)
     */
    @GetMapping("/entitys")
    public List<Post>  getPostEntity(@PathVariable Long id)  { return postService.getEntity(); }

    /**
     * 7. DTO 반환 (정상)
     */
    @GetMapping("/{id}/dto")
    public PostResponse getPostDto(@PathVariable Long id) {
        return postService.getPost(id);
    }

    /**
     * 8. 게시글 목록 (페이징)
     */
    @GetMapping
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postService.getPosts(pageable);
    }

    /**
     * 9. 게시글 생성 (Cascade.PERSIST)
     */
    @PostMapping
    public PostResponse createPost(@RequestBody PostCreateRequest request) {
        return postService.createPost(request);
    }

    /**
     * 10. 게시글 삭제 (Cascade.REMOVE)
     */
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "게시글과 댓글이 모두 삭제되었습니다.";
    }

    /**
     * 11. 댓글 삭제 (orphanRemoval)
     */
    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id) {
        postService.deleteComment(id);
        return "댓글이 삭제되었습니다.";
    }
}
