package io.goorm.jpa.dto.board;

import io.goorm.jpa.entity.Board;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO
 */
public record BoardResponse(
        @Schema(description = "게시글 번호", example = "1")
        Long boardNo,
        
        @Schema(description = "제목", example = "Spring Boot 학습 가이드")
        String title,
        
        @Schema(description = "내용", example = "Spring Boot를 이용한 웹 애플리케이션 개발 방법을 설명합니다.")
        String content,
        
        @Schema(description = "조회수", example = "15")
        Integer viewCount,
        
        @Schema(description = "작성자 번호", example = "2")
        Long authorNo,
        
        @Schema(description = "작성자명", example = "김작성")
        String authorName,
        
        @Schema(description = "생성일시", example = "2024-01-01T09:00:00")
        LocalDateTime createdAt,
        
        @Schema(description = "수정일시", example = "2024-01-01T09:00:00")
        LocalDateTime updatedAt
) {
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getBoardNo(),
                board.getTitle(),
                board.getContent(),
                board.getViewCount(),
                board.getAuthor().getUserNo(),
                board.getAuthor().getFullName(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
