package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * Enrollment Repository
 * 기본 CRUD, QueryDSL은 EnrollmentQueryRepository 사용
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * 학생과 강의로 수강신청 조회
     */
    Optional<Enrollment> findByStudentAndCourseAndDeletedFalse(User student, Course course);

    /**
     * 중복 수강신청 확인
     */
    boolean existsByStudentAndCourseAndDeletedFalse(User student, Course course);

    /**
     * 삭제되지 않은 수강신청 수
     */
    Long countByDeletedFalse();

    /**
     * 강의와 상태로 수강신청 목록 조회
     */
    List<Enrollment> findByCourseAndStatusAndDeletedFalse(Course course, EnrollmentStatus status);

    // ===== Step 2: 비관적 락(Pessimistic Lock) 메서드들 =====

    /**
     * 수강신청 조회 (비관적 락 - 승인/거절 시 동시성 제어)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.enrollmentNo = :enrollmentNo
        AND e.deleted = false
        """)
    Optional<Enrollment> findByIdForUpdate(@Param("enrollmentNo") Long enrollmentNo);

    /**
     * 학생의 수강신청 조회 (비관적 락 - 중복 신청 방지)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.student = :student
        AND e.course = :course
        AND e.deleted = false
        """)
    Optional<Enrollment> findByStudentAndCourseForUpdate(@Param("student") User student, 
                                                         @Param("course") Course course);

    /**
     * 강의의 수강신청 목록 조회 (비관적 락 - 일괄 처리 시 동시성 제어)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.course = :course
        AND e.status = :status
        AND e.deleted = false
        ORDER BY e.createdAt ASC
        """)
    List<Enrollment> findByCourseAndStatusForUpdate(@Param("course") Course course, 
                                                    @Param("status") EnrollmentStatus status);

    /**
     * 대기 중인 수강신청 조회 (비관적 락 - 승인 처리 시 동시성 제어)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.course = :course
        AND e.status = 'PENDING'
        AND e.deleted = false
        ORDER BY e.createdAt ASC
        """)
    List<Enrollment> findPendingEnrollmentsForUpdate(@Param("course") Course course);
}
