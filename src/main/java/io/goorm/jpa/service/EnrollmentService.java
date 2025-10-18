package io.goorm.jpa.service;

import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.EnrollmentQueryRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import io.goorm.jpa.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Enrollment Service
 * - QueryDSL 사용
 * - Optimistic Lock (재시도 로직)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentQueryRepository enrollmentQueryRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 수강신청 (Optimistic Lock + 재시도)
     */
    @Transactional
    public EnrollmentResponse enroll(EnrollmentCreateRequest request) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(request.courseNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getDeleted()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 중복 수강신청 확인
        if (enrollmentRepository.existsByStudentAndCourseAndDeletedFalse(currentUser, course)) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        // Optimistic Lock 재시도
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                return attemptEnroll(currentUser, course);
            } catch (OptimisticLockException e) {
                retryCount++;
                log.warn("Optimistic lock conflict on enrollment. Retry {}/{}", retryCount, MAX_RETRY_COUNT);

                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new BusinessException(ErrorCode.ENROLLMENT_RETRY_EXCEEDED);
                }

                // 잠시 대기 후 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(ErrorCode.ENROLLMENT_RETRY_EXCEEDED);
                }

                // Course 재조회
                course = courseRepository.findById(request.courseNo())
                        .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
            }
        }

        throw new BusinessException(ErrorCode.ENROLLMENT_RETRY_EXCEEDED);
    }

    /**
     * 수강신청 시도
     */
    private EnrollmentResponse attemptEnroll(User student, Course course) {
        // 정원 확인
        if (!course.isAvailable()) {
            throw new BusinessException(ErrorCode.COURSE_FULL);
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created: enrollmentNo={}, student={}, course={}",
                savedEnrollment.getEnrollmentNo(), student.getUsername(), course.getName());

        return EnrollmentResponse.from(savedEnrollment);
    }

    /**
     * 수강신청 취소 (대기 상태만)
     */
    @Transactional
    public void cancel(Long enrollmentNo) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getDeleted()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        User currentUser = getCurrentUser();

        // 본인 확인
        if (!enrollment.getStudent().equals(currentUser)) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // 대기 상태만 취소 가능
        if (!enrollment.isPending()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_CANNOT_CANCEL);
        }

        enrollment.cancel();
        log.info("Enrollment cancelled: enrollmentNo={}", enrollmentNo);
    }

    /**
     * 수강신청 승인 (관리자)
     */
    @Transactional
    public EnrollmentResponse approve(Long enrollmentNo) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getDeleted()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        enrollment.approve();
        log.info("Enrollment approved: enrollmentNo={}", enrollmentNo);

        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 수강신청 거절 (관리자)
     */
    @Transactional
    public EnrollmentResponse reject(Long enrollmentNo) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getDeleted()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        enrollment.reject();
        log.info("Enrollment rejected: enrollmentNo={}", enrollmentNo);

        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 내 수강신청 목록
     */
    public Page<EnrollmentResponse> getMyEnrollments(Pageable pageable) {
        User currentUser = getCurrentUser();

        return enrollmentQueryRepository.findByStudent(currentUser, pageable)
                .map(EnrollmentResponse::from);
    }

    /**
     * 수강신청 관리 (관리자, 동적 검색)
     */
    public Page<EnrollmentResponse> search(Long studentNo, Long courseNo, EnrollmentStatus status, Pageable pageable) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        return enrollmentQueryRepository.search(studentNo, courseNo, status, pageable)
                .map(EnrollmentResponse::from);
    }

    /**
     * 현재 로그인한 사용자 조회
     */
    private User getCurrentUser() {
        String userNo = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
