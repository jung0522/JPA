package io.goorm.jpa.m2m.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
    Long id,
    String title,
    String content,
    List<TagResponse> tags,
    LocalDateTime createdAt
) {}
