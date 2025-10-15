package io.goorm.jpa.m2one.dto;

public record CommentCreateRequest(
    Long postId,
    String content
) {}
