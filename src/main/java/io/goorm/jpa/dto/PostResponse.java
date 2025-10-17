package io.goorm.jpa.dto;

import io.goorm.jpa.entity.Post;

import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        List<CommentResponse> comments
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getComments().stream()
                        .map(CommentResponse::from)
                        .toList()
        );
    }
}
