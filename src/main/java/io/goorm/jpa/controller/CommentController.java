package io.goorm.jpa.controller;

import io.goorm.jpa.entity.Comment;
import io.goorm.jpa.repository.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment API", description = "댓글 API - Entity 직접 반환 에러 시연")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;

    /**
     * Entity 직접 반환 (에러 발생 시연용)
     * Comment.post가 LAZY 프록시라서 JSON 직렬화 시 에러 발생!
     */
    @Operation(
            summary = "Entity 직접 반환 (에러 발생!)",
            description = "Comment Entity 직접 반환 - LAZY 프록시(ByteBuddyInterceptor) 직렬화 에러 발생! DTO 사용 필수"
    )
    @GetMapping("/{id}/entity")
    public Comment getCommentEntity(@PathVariable Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
    }
}
