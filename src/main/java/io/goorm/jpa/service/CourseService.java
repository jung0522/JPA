package io.goorm.jpa.service;

import io.goorm.jpa.dto.course.CourseCreateRequest;
import io.goorm.jpa.dto.course.CourseResponse;
import io.goorm.jpa.dto.course.CourseUpdateRequest;
import io.goorm.jpa.dto.course.CourseSearchCondition;
import io.goorm.jpa.dto.curriculum.CurriculumResponse;
import io.goorm.jpa.dto.user.UserResponse;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.UserRepository;
import io.goorm.jpa.repository.CurriculumRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import io.goorm.jpa.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course Service
 * - JPQL 사용
 * - ManyToOne 단방향
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CurriculumRepository curriculumRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 강의 생성 (강사만)
     */
    @Transactional
    public CourseResponse create(CourseCreateRequest request) {
        User currentUser = getCurrentUser();

        // 강사 권한 확인
        if (!currentUser.isInstructor() && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        Course course = Course.builder()
                .name(request.name())
                .description(request.description())
                .maxStudents(request.maxStudents())
                .instructor(currentUser)
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Course created: courseNo={}, instructor={}", savedCourse.getCourseNo(), currentUser.getUsername());

        return CourseResponse.from(savedCourse);
    }

    /**
     * 강의 목록 조회 (Fetch Join)
     */
    public Page<CourseResponse> getList(Pageable pageable) {
        return courseRepository.findAllWithInstructor(pageable)
                .map(CourseResponse::from);
    }

    /**
     * 수강 가능한 강의 검색 (JPQL)
     */
    public Page<CourseResponse> searchAvailableCourses(String keyword, Pageable pageable) {
        return courseRepository.searchAvailableCourses(keyword, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 강의 상세 조회 (Fetch Join)
     */
    public CourseResponse getDetail(Long courseNo) {
        Course course = courseRepository.findByIdWithInstructor(courseNo);

        if (course == null || course.getDeleted()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        return CourseResponse.from(course);
    }

    /**
     * 강의 수정 (강사 본인만)
     */
    @Transactional
    public CourseResponse update(Long courseNo, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getDeleted()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }

        User currentUser = getCurrentUser();

        // 강사 본인 또는 관리자만 수정 가능
        if (!course.isInstructor(currentUser) && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        course.update(request.name(), request.description(), request.maxStudents());
        log.info("Course updated: courseNo={}, instructor={}", courseNo, currentUser.getUsername());

        return CourseResponse.from(course);
    }

    /**
     * 강의 삭제 (Step 2에서 구현 - Cascade 필요)
     * Step 1에서는 구현하지 않음
     */
    @Transactional
    public void delete(Long courseNo) {
        // Step 1: 양방향 없이 삭제는 위험 (주차/차시 고아 데이터)
        // Step 2에서 Cascade로 구현
        throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
    }

    /**
     * 내 강의 목록 (강사)
     */
    public Page<CourseResponse> getMyCourses(Pageable pageable) {
        User currentUser = getCurrentUser();

        if (!currentUser.isInstructor() && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.COURSE_FORBIDDEN);
        }

        return courseRepository.findByInstructor(currentUser, pageable)
                .map(CourseResponse::from);
    }

    // ===== 복잡한 검색 메서드들 (JPQL) =====

    /**
     * 강의명과 강사명으로 통합 검색
     */
    public Page<CourseResponse> searchByCourseAndInstructor(String courseName, String instructorName, Pageable pageable) {
        return courseRepository.searchByCourseAndInstructor(courseName, instructorName, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 정원 범위로 검색
     */
    public Page<CourseResponse> findByCapacityRange(Integer minCapacity, Integer maxCapacity, Pageable pageable) {
        return courseRepository.findByCapacityRange(minCapacity, maxCapacity, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 수강 가능한 강의만 검색
     */
    public Page<CourseResponse> findAvailableCourses(String keyword, Pageable pageable) {
        return courseRepository.findAvailableCourses(keyword, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 인기 강의 검색 (수강생 수 기준)
     */
    public Page<CourseResponse> findPopularCourses(Integer minStudents, Pageable pageable) {
        return courseRepository.findPopularCourses(minStudents, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 최근 개설된 강의 검색
     */
    public Page<CourseResponse> findRecentCourses(java.time.LocalDateTime startDate, Pageable pageable) {
        return courseRepository.findRecentCourses(startDate, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 강의 상태별 검색 (수강 가능/마감)
     */
    public Page<CourseResponse> findByAvailability(Boolean isAvailable, Pageable pageable) {
        return courseRepository.findByAvailability(isAvailable, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 정원 대비 수강률이 높은 강의
     */
    public Page<CourseResponse> findHighEnrollmentCourses(Double minRatio, Pageable pageable) {
        return courseRepository.findHighEnrollmentCourses(minRatio, pageable)
                .map(CourseResponse::from);
    }

    /**
     * 복합 조건 검색
     */
    public Page<CourseResponse> findCoursesByComplexConditions(String courseName, String instructorName, 
                                                              Integer minCapacity, Integer maxCapacity, 
                                                              Boolean isAvailable, Pageable pageable) {
        return courseRepository.findCoursesByComplexConditions(courseName, instructorName, minCapacity, maxCapacity, isAvailable, pageable)
                .map(CourseResponse::from);
    }


    /**
     * 동적 조건 검색 (SearchCondition DTO 사용)
     */
    public Page<CourseResponse> searchCourses(CourseSearchCondition condition, Pageable pageable) {
        Page<Course> page = courseRepository.findCoursesByDynamicConditions(
            condition.getCourseName(),
            condition.getInstructorName(),
            condition.getDescription(),
            condition.getMinCapacity(),
            condition.getMaxCapacity(),
            condition.getMinCurrentStudents(),
            condition.getMaxCurrentStudents(),
            condition.getIsAvailable(),
            condition.getMinEnrollmentRatio(),
            condition.getStartDate(),
            condition.getEndDate(),
            condition.getSortBy(),
            pageable
        );
        return page.map(CourseResponse::from);
    }


    /**
     * 강의 커리큘럼 조회 (Step 1: 팝업용)
     */
    public List<CurriculumResponse> getCurriculums(Long courseNo) {
        Course course = courseRepository.findById(courseNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        
        return curriculumRepository.findByCourseNoOrderByWeekNumber(courseNo)
                .stream()
                .map(CurriculumResponse::from)
                .toList();
    }

    /**
     * 강의 수강생 목록 조회 (Step 1: 팝업용)
     */
    public List<UserResponse> getEnrolledStudents(Long courseNo) {
        Course course = courseRepository.findById(courseNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        
        return enrollmentRepository.findByCourseAndStatusAndDeletedFalse(course, EnrollmentStatus.APPROVED)
                .stream()
                .map(enrollment -> UserResponse.from(enrollment.getStudent()))
                .toList();
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
