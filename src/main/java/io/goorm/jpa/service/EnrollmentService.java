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
     * 수강신청 - Step 2: 비관적 락 + Optimistic Lock 하이브리드
     */
    @Transactional
    public EnrollmentResponse enroll(EnrollmentCreateRequest request) {
        log.info("수강신청 시작: courseNo={}", request.courseNo());
        
        User currentUser = getCurrentUser();
        log.info("현재 사용자: userNo={}, username={}", currentUser.getUserNo(), currentUser.getUsername());

        // Step 2: 비관적 락으로 강의 조회 (동시성 제어)
        Course course = courseRepository.findAvailableByIdForEnrollment(request.courseNo());
        
        if (course == null) {
            log.error("강의를 찾을 수 없거나 정원 초과: courseNo={}", request.courseNo());
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        log.info("강의 정보: courseNo={}, name={}, currentStudents={}, maxStudents={}", 
                course.getCourseNo(), course.getName(), course.getCurrentStudents(), course.getMaxStudents());

        // Step 2: 비관적 락으로 중복 수강신청 확인
        if (enrollmentRepository.findByStudentAndCourseForUpdate(currentUser, course).isPresent()) {
            log.error("중복 수강신청: studentNo={}, courseNo={}", currentUser.getUserNo(), course.getCourseNo());
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        // Step 2: 양방향 관계 + 편의 메서드 사용
        Enrollment enrollment = Enrollment.builder()
                .student(currentUser)
                .course(course)
                .build();

        // Step 2: Course의 편의 메서드로 양방향 관계 설정
        course.addEnrollment(enrollment);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created: enrollmentNo={}, student={}, course={}",
                savedEnrollment.getEnrollmentNo(), currentUser.getUsername(), course.getName());

        return EnrollmentResponse.from(savedEnrollment);
    }


    /**
     * 수강신청 취소 (대기 상태만) - Step 2: 비관적 락 적용
     */
    @Transactional
    public void cancel(Long enrollmentNo) {
        User currentUser = getCurrentUser();

        // Step 2: 비관적 락으로 수강신청 조회
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getDeleted()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // 본인 또는 관리자만 취소 가능
        if (!enrollment.getStudent().equals(currentUser) && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // 대기 상태만 취소 가능
        if (!enrollment.isPending()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_CANNOT_CANCEL);
        }

        // Step 2: Course의 편의 메서드로 양방향 관계 해제
        enrollment.getCourse().removeEnrollment(enrollment);
        enrollment.cancel();
        log.info("Enrollment cancelled: enrollmentNo={}", enrollmentNo);
    }

    /**
     * 수강신청 승인 (관리자) - Step 2: 비관적 락 적용
     */
    @Transactional
    public EnrollmentResponse approve(Long enrollmentNo) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin() && !currentUser.isInstructor()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // Step 2: 비관적 락으로 수강신청 조회
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getDeleted()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        enrollment.approve();
        log.info("Enrollment approved: enrollmentNo={}", enrollmentNo);

        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 수강신청 거절 (관리자) - Step 2: 비관적 락 적용
     */
    @Transactional
    public EnrollmentResponse reject(Long enrollmentNo) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin() && !currentUser.isInstructor()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // Step 2: 비관적 락으로 수강신청 조회
        Enrollment enrollment = enrollmentRepository.findByIdForUpdate(enrollmentNo)
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
    public Page<EnrollmentResponse> searchByAdminConditions(String searchField, String keyword, Boolean include, EnrollmentStatus status, Pageable pageable) {
        User currentUser = getCurrentUser();

        if (!currentUser.isAdmin() && !currentUser.isInstructor()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        return enrollmentQueryRepository.searchByAdminConditions(searchField, keyword, include, status, pageable)
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
