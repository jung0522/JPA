package io.goorm.jpa.m2one.dto;

public record CommentResponse(
    Long id,
    String content,
    Long postId,
    String postTitle
) {}
