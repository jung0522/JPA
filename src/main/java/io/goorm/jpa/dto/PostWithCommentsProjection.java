package io.goorm.jpa.dto;

import java.util.List;

/**
 * DTO Projection (Interface 기반)
 * JPA가 자동으로 구현체 생성
 */
public interface PostWithCommentsProjection {
    Long getId();
    String getTitle();
    String getContent();
    List<CommentProjection> getComments();

    interface CommentProjection {
        Long getId();
        String getContent();
    }
}
