package io.goorm.jpa.m2one.service;

import io.goorm.jpa.m2one.dto.*;
import io.goorm.jpa.m2one.entity.Comment;
import io.goorm.jpa.m2one.entity.Post;
import io.goorm.jpa.m2one.repository.CommentRepository;
import io.goorm.jpa.m2one.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // N+1 발생 버전 (의도적)
    public List<CommentResponse> getCommentsNPlusOne() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getPost().getId(),
                        c.getPost().getTitle()
                ))
                .collect(Collectors.toList());
    }

    // Fetch Join 해결 버전
    public List<CommentResponse> getCommentsWithFetchJoin() {
        List<Comment> comments = commentRepository.findAllWithPost();
        return comments.stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getPost().getId(),
                        c.getPost().getTitle()
                ))
                .collect(Collectors.toList());
    }

    // DTO 프로젝션 (페이징)
    public Page<CommentWithPostDto> getCommentsDto(Pageable pageable) {
        return commentRepository.findCommentsWithPost(pageable);
    }

    // 단건 조회
    public CommentResponse getComment(Long id) {
        Comment c = commentRepository.findById(id).orElseThrow();
        return new CommentResponse(
                c.getId(), c.getContent(), c.getPost().getId(), c.getPost().getTitle()
        );
    }

    // 게시글별 댓글 개수
    public Long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // 게시글별 댓글 목록(페이징)
    public Page<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> page = commentRepository.findByPostId(postId, pageable);
        return page.map(c -> new CommentResponse(
                c.getId(), c.getContent(), c.getPost().getId(), c.getPost().getTitle()
        ));
    }

    // 생성
    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Post post = postRepository.findById(request.postId()).orElseThrow();
        Comment comment = new Comment(request.content(), post);
        comment = commentRepository.save(comment);
        return new CommentResponse(comment.getId(), comment.getContent(), post.getId(), post.getTitle());
    }

    // 수정
    @Transactional
    public CommentResponse update(Long id, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.updateContent(request.content());
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getPost().getId(), comment.getPost().getTitle());
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}
