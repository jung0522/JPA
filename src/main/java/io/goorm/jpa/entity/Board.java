package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시판 엔티티
 * - ManyToOne 단방향 (Board → User)
 * - Query Methods 사용
 * - Soft Delete 지원
 */
@Entity
@Getter
@ToString(exclude = "author")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long boardNo;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_no", nullable = false)
    private User author;

    @Builder
    public Board(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = 0;
    }

    /**
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /**
     * 작성자 확인
     */
    public boolean isAuthor(User user) {
        return this.author.equals(user);
    }
}
