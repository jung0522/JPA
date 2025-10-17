package io.goorm.jpa.m2m.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ⭐ 중간 엔티티 (Join Table Entity)
 *
 * Post와 Tag의 다대다 관계를 "일대다 + 다대일"로 분리
 *
 * 장점:
 * 1. 추가 필드 자유롭게 추가 가능 (taggedAt, order, taggedBy 등)
 * 2. 비즈니스 로직 추가 가능 (updateOrder 등)
 * 3. 쿼리 제어 완벽
 */
@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 다대일: PostTag → Post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // ✅ 다대일: PostTag → Tag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // ✅ 추가 필드 1: 태그 추가 날짜
    @Column(nullable = false)
    private LocalDateTime taggedAt;

    // ✅ 추가 필드 2: 태그 순서 (과제용)
    private Integer displayOrder;

    // ✅ 추가 필드 3: 누가 추가했는지 (과제용)
    private String taggedBy;

    // 기본 생성자
    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        this.taggedAt = LocalDateTime.now();
    }

    // 모든 필드를 받는 생성자 (과제용)
    public PostTag(Post post, Tag tag, String taggedBy) {
        this.post = post;
        this.tag = tag;
        this.taggedAt = LocalDateTime.now();
        this.taggedBy = taggedBy;
    }

    // ✅ 비즈니스 로직: 순서 변경
    public void updateOrder(Integer newOrder) {
        this.displayOrder = newOrder;
    }

    // ✅ 비즈니스 로직: 추가자 설정
    public void setTaggedBy(String taggedBy) {
        this.taggedBy = taggedBy;
    }
}
