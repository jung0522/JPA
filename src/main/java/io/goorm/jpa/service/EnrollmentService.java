package io.goorm.jpa.service;

import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentRequest;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    
    // 배치 처리 메시지 상수
    private static final String BATCH_APPROVE_SUCCESS_MESSAGE = "수강신청 승인 완료";
    private static final String BATCH_REJECT_SUCCESS_MESSAGE = "수강신청 거절 완료";

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

    // ===== Step 2-4: 배치 처리 메서드들 =====

    /**
     * 수강신청 일괄 승인/거절
     */
    @Transactional
    public BatchEnrollmentResponse batchProcessEnrollments(BatchEnrollmentRequest request) {
        log.info("배치 처리 시작: action={}, count={}", request.getAction(), request.getEnrollmentCount());
        
        User currentUser = getCurrentUser();
        
        // 관리자 또는 강사 권한 확인
        if (!currentUser.isAdmin() && !currentUser.isInstructor()) {
            throw new BusinessException(ErrorCode.USER_FORBIDDEN);
        }

        // 요청 유효성 검증
        if (!request.hasEnrollments()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // 비관적 락으로 수강신청 일괄 조회
        List<Enrollment> enrollments = enrollmentRepository.findByIdsForBatchUpdate(request.getEnrollmentIds());
        
        if (enrollments.isEmpty()) {
            log.warn("처리 가능한 수강신청이 없음: requested={}, found=0", request.getEnrollmentCount());
            throw new BusinessException(ErrorCode.ENROLLMENT_BATCH_NO_ITEMS);
        }

        // 개별 처리 결과 수집
        List<BatchEnrollmentResponse.EnrollmentProcessResult> results = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            try {
                processEnrollment(enrollment, request.getAction(), request.getReason());
                
                results.add(BatchEnrollmentResponse.EnrollmentProcessResult.builder()
                    .enrollmentId(enrollment.getEnrollmentNo())
                    .success(true)
                    .message(request.isApprove() ? BATCH_APPROVE_SUCCESS_MESSAGE : BATCH_REJECT_SUCCESS_MESSAGE)
                    .build());
                    
                log.info("수강신청 처리 완료: enrollmentNo={}, action={}", 
                    enrollment.getEnrollmentNo(), request.getAction());
                    
            } catch (Exception e) {
                results.add(BatchEnrollmentResponse.EnrollmentProcessResult.builder()
                    .enrollmentId(enrollment.getEnrollmentNo())
                    .success(false)
                    .error(e.getMessage())
                    .build());
                    
                log.error("수강신청 처리 실패: enrollmentNo={}, error={}", 
                    enrollment.getEnrollmentNo(), e.getMessage());
            }
        }

        // 응답 생성
        BatchEnrollmentResponse response = BatchEnrollmentResponse.success(
            request.getAction(), 
            request.getEnrollmentCount(), 
            results
        );

        log.info("배치 처리 완료: total={}, success={}, failure={}", 
            response.getTotalCount(), response.getSuccessCount(), response.getFailureCount());

        return response;
    }

    /**
     * 강의별 수강신청 일괄 승인/거절
     */
    @Transactional
    public BatchEnrollmentResponse batchProcessEnrollmentsByCourse(Long courseNo, BatchEnrollmentRequest.BatchAction action, String reason) {
        log.info("강의별 배치 처리 시작: courseNo={}, action={}", courseNo, action);
        
        User currentUser = getCurrentUser();
        
        // 강의 조회 및 권한 확인
        Course course = courseRepository.findById(courseNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
            
        if (!course.isInstructor(currentUser) && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        // 강의별 대기 중인 수강신청 조회
        List<Enrollment> enrollments = enrollmentRepository.findPendingByCourseForBatchUpdate(course);
        
        if (enrollments.isEmpty()) {
            log.warn("강의별 처리 가능한 수강신청이 없음: courseNo={}", courseNo);
            throw new BusinessException(ErrorCode.ENROLLMENT_BATCH_NO_ITEMS);
        }

        // 수강신청 ID 목록 생성
        List<Long> enrollmentIds = enrollments.stream()
            .map(Enrollment::getEnrollmentNo)
            .collect(Collectors.toList());

        // 배치 처리 요청 생성
        BatchEnrollmentRequest request = BatchEnrollmentRequest.builder()
            .enrollmentIds(enrollmentIds)
            .action(action)
            .reason(reason)
            .build();

        return batchProcessEnrollments(request);
    }

    /**
     * 개별 수강신청 처리
     */
    private void processEnrollment(Enrollment enrollment, BatchEnrollmentRequest.BatchAction action, String reason) {
        // 대기 상태가 아닌 수강신청은 처리할 수 없음
        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_PROCESSED);
        }
        
        if (action == BatchEnrollmentRequest.BatchAction.APPROVE) {
            enrollment.approve();
        } else if (action == BatchEnrollmentRequest.BatchAction.REJECT) {
            enrollment.reject();
        }
        
        // 처리 사유가 있으면 로그에 기록
        if (reason != null && !reason.trim().isEmpty()) {
            log.info("수강신청 처리 사유: enrollmentNo={}, reason={}", 
                enrollment.getEnrollmentNo(), reason);
        }
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
