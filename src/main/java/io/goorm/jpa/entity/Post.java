package io.goorm.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_m2o_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title", nullable = false, length = 200)
    private String title;

    @Column(name = "post_content", columnDefinition = "TEXT")
    private String content;

    // Cascade & orphanRemoval 시연용
    // @BatchSize: N+1 해결을 위한 배치 사이즈 설정 (IN 절로 한 번에 조회)
    //@org.hibernate.annotations.BatchSize(size = 10)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
