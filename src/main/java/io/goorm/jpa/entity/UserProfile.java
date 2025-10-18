package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 프로필 엔티티
 * - OneToOne 단방향 (UserProfile → User)
 * - Step 1: 단방향만
 * - Step 2: 양방향 + Cascade
 */
@Entity
@Getter
@ToString(exclude = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_profile")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long profileNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", unique = true, nullable = false)
    private User user;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 500)
    private String bio;

    @Builder
    public UserProfile(User user, String phone, String address, String bio) {
        this.user = user;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
    }

    /**
     * 프로필 수정
     */
    public void update(String phone, String address, String bio) {
        this.phone = phone;
        this.address = address;
        this.bio = bio;
    }
}
