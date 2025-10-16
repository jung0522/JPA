package io.goorm.jpa.m2one.controller;

import io.goorm.jpa.m2one.dto.*;
import io.goorm.jpa.m2one.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/m2one/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // N+1 발생 버전 (기본)
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments() {
        // 기본: N+1 발생
        return ResponseEntity.ok(commentService.getCommentsNPlusOne());

        // Alt) Fetch Join 버전 (주석 해제하여 사용)
        // return ResponseEntity.ok(commentService.getCommentsWithFetchJoin());
    }

    // JOIN vs FETCH JOIN 비교용 엔드포인트
    @GetMapping("/with-join")
    public ResponseEntity<List<CommentResponse>> getCommentsWithJoin() {
        // 일반 JOIN: N+1 발생 (WHERE 조건용)
        return ResponseEntity.ok(commentService.getCommentsWithJoin());
    }

    @GetMapping("/with-fetch-join")
    public ResponseEntity<List<CommentResponse>> getCommentsWithFetchJoin() {
        // FETCH JOIN: N+1 해결 (즉시 로딩)
        return ResponseEntity.ok(commentService.getCommentsWithFetchJoin());
    }

    // DTO 프로젝션 (페이징)
    @GetMapping("/dto")
    public ResponseEntity<Page<CommentWithPostDto>> getCommentsDto(Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsDto(pageable));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }

    // 게시글별 댓글 개수
    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<Long> getCountByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.countByPostId(postId));
    }

    // 게시글별 댓글 목록(페이징)
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentResponse>> getByPost(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId, pageable));
    }

    // 생성
    @PostMapping
    public ResponseEntity<CommentResponse> create(@RequestBody CommentCreateRequest request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id, @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.update(id, request));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
