package io.goorm.jpa.m2one.dto;

public record CommentWithPostDto(
    Long commentId,
    String content,
    Long postId,
    String postTitle
) {}
