package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
