package io.goorm.jpa.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 강의 검색 조건 DTO
 * - QueryDSL 동적 검색용
 * - 공통 조건 모듈과 연동
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSearchCondition {

    // ===== 기본 검색 조건 =====
    private String courseName;
    private String instructorName;
    private String description;

    // ===== 정원 조건 =====
    private Integer minCapacity;
    private Integer maxCapacity;

    // ===== 수강생 수 조건 =====
    private Integer minCurrentStudents;
    private Integer maxCurrentStudents;

    // ===== 수강률 조건 =====
    private Double minEnrollmentRatio;
    private Double maxEnrollmentRatio;

    // ===== 날짜 조건 =====
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // ===== 상태 조건 =====
    private Boolean isAvailable; // 수강 가능 여부

    // ===== 정렬 조건 =====
    private String sortBy; // name, nameDesc, capacity, capacityDesc, students, studentsDesc, enrollmentRatio, created, createdDesc

    // ===== 페이지 조건 =====
    private Integer page;
    private Integer size;

    // ===== 유틸리티 메서드 =====

    /**
     * 강의명 검색 여부
     */
    public boolean hasCourseName() {
        return courseName != null && !courseName.trim().isEmpty();
    }

    /**
     * 강사명 검색 여부
     */
    public boolean hasInstructorName() {
        return instructorName != null && !instructorName.trim().isEmpty();
    }

    /**
     * 설명 검색 여부
     */
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    /**
     * 정원 범위 검색 여부
     */
    public boolean hasCapacityRange() {
        return minCapacity != null || maxCapacity != null;
    }

    /**
     * 수강생 수 범위 검색 여부
     */
    public boolean hasCurrentStudentsRange() {
        return minCurrentStudents != null || maxCurrentStudents != null;
    }

    /**
     * 수강률 범위 검색 여부
     */
    public boolean hasEnrollmentRatioRange() {
        return minEnrollmentRatio != null || maxEnrollmentRatio != null;
    }

    /**
     * 날짜 범위 검색 여부
     */
    public boolean hasDateRange() {
        return startDate != null || endDate != null;
    }

    /**
     * 정렬 조건 여부
     */
    public boolean hasSortBy() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    /**
     * 기본 정렬 조건 반환
     */
    public String getSortByOrDefault() {
        return hasSortBy() ? sortBy : "createdDesc";
    }

    /**
     * 검색 조건이 있는지 확인
     */
    public boolean hasAnyCondition() {
        return hasCourseName() || hasInstructorName() || hasDescription() ||
               hasCapacityRange() || hasCurrentStudentsRange() || hasEnrollmentRatioRange() ||
               hasDateRange() || isAvailable != null;
    }

    /**
     * 빌더 패턴을 위한 정적 팩토리 메서드
     */
    public static CourseSearchConditionBuilder builder() {
        return new CourseSearchConditionBuilder();
    }

    /**
     * 기본 검색 조건으로 생성
     */
    public static CourseSearchCondition of(String courseName, String instructorName) {
        return CourseSearchCondition.builder()
            .courseName(courseName)
            .instructorName(instructorName)
            .sortBy("createdDesc")
            .build();
    }

    /**
     * 정원 범위 검색 조건으로 생성
     */
    public static CourseSearchCondition byCapacity(Integer minCapacity, Integer maxCapacity) {
        return CourseSearchCondition.builder()
            .minCapacity(minCapacity)
            .maxCapacity(maxCapacity)
            .sortBy("capacityDesc")
            .build();
    }

    /**
     * 수강 가능한 강의만 검색
     */
    public static CourseSearchCondition availableOnly() {
        return CourseSearchCondition.builder()
            .isAvailable(true)
            .sortBy("createdDesc")
            .build();
    }

    /**
     * 인기 강의 검색 (수강률 높은 순)
     */
    public static CourseSearchCondition popularCourses() {
        return CourseSearchCondition.builder()
            .minEnrollmentRatio(0.5) // 50% 이상
            .sortBy("enrollmentRatio")
            .build();
    }

    /**
     * 최근 개설된 강의 검색
     */
    public static CourseSearchCondition recentCourses(int days) {
        return CourseSearchCondition.builder()
            .startDate(LocalDateTime.now().minusDays(days))
            .sortBy("createdDesc")
            .build();
    }
}