package io.goorm.jpa.dto.board;

import io.goorm.jpa.entity.Board;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO
 */
public record BoardResponse(
        Long boardNo,
        String title,
        String content,
        Integer viewCount,
        Long authorNo,
        String authorName,
        LocalDateTime createdAt,
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
