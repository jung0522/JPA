package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import io.goorm.jpa.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * 수강신청 엔티티
 * - ManyToOne 단방향 (Enrollment → User, Course)
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
     */
    public void approve() {
        this.status = EnrollmentStatus.APPROVED;
        this.course.increaseCurrentStudents();
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
}
