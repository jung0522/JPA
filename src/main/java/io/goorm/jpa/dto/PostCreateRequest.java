package io.goorm.jpa.dto;

import java.util.List;

public record PostCreateRequest(
        String title,
        String content,
        List<String> commentContents  // Cascade 테스트용
) {
}
