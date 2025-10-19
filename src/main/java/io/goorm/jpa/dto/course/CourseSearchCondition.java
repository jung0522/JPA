package io.goorm.jpa.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 강의 검색 조건 DTO
 * JPQL 동적 쿼리용
 */
@Data
@Builder
public class CourseSearchCondition {

    // 강의 기본 정보
    @Schema(description = "강의명", example = "Spring Boot")
    private String courseName;
    
    @Schema(description = "강의 설명", example = "웹 개발")
    private String description;
    
    // 강사 정보
    @Schema(description = "강사명", example = "김강사")
    private String instructorName;
    
    // 정원 관련
    @Schema(description = "최소 정원", example = "10")
    private Integer minCapacity;
    
    @Schema(description = "최대 정원", example = "50")
    private Integer maxCapacity;
    
    // 수강생 관련
    @Schema(description = "최소 현재 수강생 수", example = "5")
    private Integer minCurrentStudents;
    
    @Schema(description = "최대 현재 수강생 수", example = "30")
    private Integer maxCurrentStudents;
    
    // 수강 가능 여부
    @Schema(description = "수강 가능 여부", example = "true")
    private Boolean isAvailable;
    
    // 수강률 관련
    @Schema(description = "최소 수강률", example = "0.5")
    private Double minEnrollmentRatio;
    
    @Schema(description = "최대 수강률", example = "1.0")
    private Double maxEnrollmentRatio;
    
    // 날짜 관련
    @Schema(description = "시작 날짜", example = "2024-01-01T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "종료 날짜", example = "2024-12-31T23:59:59")
    private LocalDateTime endDate;
    
    // 정렬
    @Schema(description = "정렬 기준", example = "created", allowableValues = {"name", "nameDesc", "capacity", "capacityDesc", "students", "studentsDesc", "created", "createdDesc"})
    private String sortBy;
}
