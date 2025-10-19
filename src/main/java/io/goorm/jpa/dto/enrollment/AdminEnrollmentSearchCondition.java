package io.goorm.jpa.dto.enrollment;

import io.goorm.jpa.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자용 수강신청 검색 조건
 * QueryDSL 동적 쿼리용
 */
@Data
@Builder
public class AdminEnrollmentSearchCondition {

    // 학생 관련
    private String studentName;

    // 강의 관련
    private String courseName;
    private String instructorName;
    private Integer minCapacity;
    private Integer maxCapacity;

    // 수강신청 관련
    private EnrollmentStatus status;
    private List<EnrollmentStatus> statusList;

    // 날짜 관련
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 수강률 관련
    private Double minEnrollmentRatio;
    private Double maxEnrollmentRatio;

    // 정렬
    private String sortBy;
}
