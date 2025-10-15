package io.goorm.jpa.m2m.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_tags", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "order_value")
    private Integer order;
    
    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
        this.order = 0;
    }
    
    public PostTag(Post post, Tag tag, LocalDateTime createdAt, String status, Integer order) {
        this.post = post;
        this.tag = tag;
        this.createdAt = createdAt;
        this.status = status;
        this.order = order;
    }
    
    public void updateOrder(Integer order) {
        this.order = order;
    }
    
    public void updateStatus(String status) {
        this.status = status;
    }
}
