package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import io.goorm.jpa.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * 수강신청 엔티티
 * - ManyToOne 양방향 (Enrollment ↔ User, Course)
 * - QueryDSL 사용
 * - Optimistic Lock (@Version)
 */
@Entity
@Getter
@ToString(exclude = {"student", "course"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "enrollment",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_no", "course_no"}))
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long enrollmentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_no", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_no", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status;

    @Version
    private Long version;  // Optimistic Lock

    @Builder
    public Enrollment(User student, Course course) {
        this.student = student;
        this.course = course;
        this.status = EnrollmentStatus.PENDING;
    }

    /**
     * 승인
     * - Step 2: Course의 편의 메서드 사용으로 변경
     */
    public void approve() {
        this.status = EnrollmentStatus.APPROVED;
        // currentStudents는 Course.addEnrollment()에서 자동으로 증가됨
    }

    /**
     * 거절
     */
    public void reject() {
        this.status = EnrollmentStatus.REJECTED;
    }

    /**
     * 취소 (대기 상태만 가능)
     */
    public void cancel() {
        if (this.status != EnrollmentStatus.PENDING) {
            throw new IllegalStateException("대기 상태만 취소 가능합니다.");
        }
        this.delete();
    }

    /**
     * 대기 상태 확인
     */
    public boolean isPending() {
        return this.status == EnrollmentStatus.PENDING;
    }

    /**
     * 승인 상태 확인
     */
    public boolean isApproved() {
        return this.status == EnrollmentStatus.APPROVED;
    }

    // ===== Step 2: 양방향 관계를 위한 setter 추가 =====
    
    /**
     * Course 설정 (양방향 관계용)
     * - Course.addEnrollment()에서 호출됨
     */
    public void setCourse(Course course) {
        this.course = course;
    }
    
    /**
     * Student 설정 (양방향 관계용)
     * - User.addEnrollment()에서 호출됨
     */
    public void setStudent(User student) {
        this.student = student;
    }
}
