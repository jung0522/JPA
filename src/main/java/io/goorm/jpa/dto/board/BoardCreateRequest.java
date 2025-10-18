package io.goorm.jpa.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 게시글 생성 요청 DTO
 */
public record BoardCreateRequest(
        @NotBlank(message = "{board.title.notblank}")
        @Size(max = 200, message = "{board.title.size}")
        String title,

        @NotBlank(message = "{board.content.notblank}")
        @Size(max = 5000, message = "{board.content.size}")
        String content
) {
}
