package io.goorm.jpa.dto.course;

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
    private String courseName;
    private String description;
    
    // 강사 정보
    private String instructorName;
    
    // 정원 관련
    private Integer minCapacity;
    private Integer maxCapacity;
    
    // 수강생 관련
    private Integer minCurrentStudents;
    private Integer maxCurrentStudents;
    
    // 수강 가능 여부
    private Boolean isAvailable;
    
    // 수강률 관련
    private Double minEnrollmentRatio;
    private Double maxEnrollmentRatio;
    
    // 날짜 관련
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // 정렬
    private String sortBy; // name, nameDesc, capacity, capacityDesc, students, studentsDesc, created, createdDesc
}
