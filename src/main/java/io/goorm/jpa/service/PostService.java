package io.goorm.jpa.service;

import io.goorm.jpa.dto.PostCreateRequest;
import io.goorm.jpa.dto.PostResponse;
import io.goorm.jpa.entity.Comment;
import io.goorm.jpa.entity.Post;
import io.goorm.jpa.repository.CommentRepository;
import io.goorm.jpa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * N+1 문제 발생 시나리오
     */
    public List<PostResponse> getPostsWithNPlusOne() {
        log.info("=== N+1 문제 발생 시나리오 ===");

        List<Post> posts = postRepository.findAll();
        log.info("게시글 조회 완료: {} 건", posts.size());

        return posts.stream()
                .map(post -> {
                    // 각 Post의 comments에 접근 → LAZY 로딩 발생 (N번 쿼리)
                    int commentCount = post.getComments().size();
                    log.info("게시글 ID {}: 댓글 {} 개", post.getId(), commentCount);
                    return PostResponse.from(post);
                })
                .toList();
    }

    /**
     * 일반 JOIN (N+1 여전히 발생 - 비교용)
     */
    public List<PostResponse> getPostsWithJoin() {
        log.info("=== 일반 JOIN (FETCH 없음) ===");

        List<Post> posts = postRepository.findAllWithJoin();
        log.info("게시글 조회 완료: {} 건", posts.size());

        return posts.stream()
                .map(post -> {
                    // JOIN은 했지만 FETCH가 없어서 여전히 LAZY 로딩 발생
                    int commentCount = post.getComments().size();
                    log.info("게시글 ID {}: 댓글 {} 개", post.getId(), commentCount);
                    return PostResponse.from(post);
                })
                .toList();
    }

    /**
     * Fetch Join으로 해결
     */
    public List<PostResponse> getPostsWithFetchJoin() {
        log.info("=== Fetch Join 해결 ===");

        List<Post> posts = postRepository.findAllWithFetchJoin();
        log.info("게시글 + 댓글 한 번에 조회 완료: {} 건", posts.size());

        return posts.stream()
                .map(post -> {
                    int commentCount = post.getComments().size();
                    log.info("게시글 ID {}: 댓글 {} 개", post.getId(), commentCount);
                    return PostResponse.from(post);
                })
                .toList();
    }

    /**
     * @EntityGraph로 해결
     */
    public List<PostResponse> getPostsWithEntityGraph() {
        log.info("=== @EntityGraph 해결 ===");

        List<Post> posts = postRepository.findAllWithEntityGraph();
        log.info("게시글 + 댓글 한 번에 조회 완료: {} 건", posts.size());

        return posts.stream()
                .map(post -> {
                    int commentCount = post.getComments().size();
                    log.info("게시글 ID {}: 댓글 {} 개", post.getId(), commentCount);
                    return PostResponse.from(post);
                })
                .toList();
    }

    /**
     * @BatchSize로 해결 (Entity에 설정)
     */
    public List<PostResponse> getPostsWithBatchSize() {
        log.info("=== @BatchSize 해결 ===");

        List<Post> posts = postRepository.findAll();
        log.info("게시글 조회 완료: {} 건", posts.size());

        // comments 접근 시 IN 절로 배치 조회
        return posts.stream()
                .map(post -> {
                    int commentCount = post.getComments().size();
                    log.info("게시글 ID {}: 댓글 {} 개", post.getId(), commentCount);
                    return PostResponse.from(post);
                })
                .toList();
    }

    /**
     * 게시글 상세 조회 (DTO 반환)
     */
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostResponse.from(post);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostResponse::from);
    }

    /**
     * 게시글 생성 (Cascade 테스트)
     */
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());

        // Cascade.PERSIST로 댓글도 함께 저장
        if (request.commentContents() != null) {
            for (String content : request.commentContents()) {
                Comment comment = new Comment(content, post);
                post.getComments().add(comment);
            }
        }

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 삭제 (Cascade.REMOVE 테스트)
     */
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // Cascade.REMOVE로 댓글도 함께 삭제
        postRepository.delete(post);
    }

    /**
     * 댓글 삭제 (orphanRemoval 테스트)
     */
    @Transactional
    public void deleteComment(Long commentId) {
        // 직접 삭제해도 orphanRemoval에 의해 관계가 끊어지면 삭제됨
        commentRepository.deleteById(commentId);
    }
}
