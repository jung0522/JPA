package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import io.goorm.jpa.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"enrollments"})
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

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 500)
    private String bio;

    // ===== Step 2: 양방향 관계 추가 =====
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder
    public User(String username, String password, String email, String fullName, UserRole role, String phone, String address, String bio) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
    }

    // 기존 코드 호환성을 위한 생성자 (phone, address, bio는 null)
    public User(String username, String password, String email, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = null;
        this.address = null;
        this.bio = null;
    }

    public void updateProfile(String email, String fullName, String phone, String address, String bio) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isInstructor() {
        return this.role == UserRole.INSTRUCTOR;
    }

    public boolean isStudent() {
        return this.role == UserRole.STUDENT;
    }

    // ===== Step 2: Enrollment 관련 편의 메서드 =====
    
    /**
     * 수강신청 추가 (편의 메서드)
     */
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }
    
    /**
     * 수강신청 제거 (편의 메서드)
     */
    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setStudent(null);
    }
    
    /**
     * 수강신청 목록 조회
     */
    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
    }
    
    /**
     * 수강신청 개수 조회
     */
    public int getEnrollmentCount() {
        return enrollments.size();
    }
    
    /**
     * 특정 강의 수강신청 여부 확인
     */
    public boolean isEnrolledIn(Course course) {
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getCourse().equals(course));
    }
    
    /**
     * 승인된 수강신청만 조회
     */
    public List<Enrollment> getApprovedEnrollments() {
        return enrollments.stream()
                .filter(Enrollment::isApproved)
                .toList();
    }
}
