package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import io.goorm.jpa.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    @EqualsAndHashCode.Include
    private Long userNo;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Builder
    public User(String username, String password, String email, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public void updateProfile(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isUser() {
        return this.role == UserRole.USER;
    }
}
