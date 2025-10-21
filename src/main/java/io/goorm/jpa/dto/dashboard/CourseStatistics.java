package io.goorm.jpa.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 강의 통계 DTO
 * - QueryDSL 통계 쿼리 결과 매핑
 * - 다양한 통계 정보 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatistics {

    // ===== 기본 정보 =====
    private Long courseNo;
    private String courseName;
    private String instructorName;
    private Long instructorNo;

    // ===== 강의 정보 =====
    private Integer maxStudents;
    private Integer currentStudents;
    private BigDecimal enrollmentRatio; // 수강률 (%)

    // ===== 통계 정보 =====
    private Long totalCourses;
    private Long totalStudents;
    private Long totalCapacity;
    private BigDecimal avgStudents;
    private BigDecimal avgCapacity;

    // ===== 수강신청 통계 =====
    private Long totalEnrollments;
    private Long approvedEnrollments;
    private Long pendingEnrollments;
    private Long rejectedEnrollments;

    // ===== 커리큘럼 통계 =====
    private Long curriculumCount;
    private Integer maxWeekNumber;

    // ===== 시간 정보 =====
    private Integer year;
    private Integer month;
    private Long monthlyCourses;
    private Long monthlyStudents;

    // ===== 생성자 (QueryDSL Projections용) =====

    /**
     * 전체 통계용 생성자
     */
    public CourseStatistics(Long totalCourses, Long totalStudents, Long totalCapacity, 
                           BigDecimal avgStudents, BigDecimal avgCapacity) {
        this.totalCourses = totalCourses;
        this.totalStudents = totalStudents;
        this.totalCapacity = totalCapacity;
        this.avgStudents = avgStudents;
        this.avgCapacity = avgCapacity;
    }

    /**
     * 강의별 상세 통계용 생성자
     */
    public CourseStatistics(Long courseNo, String courseName, String instructorName,
                           Integer maxStudents, Integer currentStudents, BigDecimal enrollmentRatio,
                           Long totalEnrollments, Long approvedEnrollments, 
                           Long pendingEnrollments, Long rejectedEnrollments) {
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.maxStudents = maxStudents;
        this.currentStudents = currentStudents;
        this.enrollmentRatio = enrollmentRatio;
        this.totalEnrollments = totalEnrollments;
        this.approvedEnrollments = approvedEnrollments;
        this.pendingEnrollments = pendingEnrollments;
        this.rejectedEnrollments = rejectedEnrollments;
    }

    /**
     * 강사별 통계용 생성자
     */
    public CourseStatistics(Long instructorNo, String instructorName,
                           Long totalCourses, Long totalStudents, Long totalCapacity,
                           BigDecimal avgStudents, Long totalEnrollments) {
        this.instructorNo = instructorNo;
        this.instructorName = instructorName;
        this.totalCourses = totalCourses;
        this.totalStudents = totalStudents;
        this.totalCapacity = totalCapacity;
        this.avgStudents = avgStudents;
        this.totalEnrollments = totalEnrollments;
    }

    /**
     * 월별 통계용 생성자
     */
    public CourseStatistics(Integer year, Integer month, Long monthlyCourses, Long monthlyStudents) {
        this.year = year;
        this.month = month;
        this.monthlyCourses = monthlyCourses;
        this.monthlyStudents = monthlyStudents;
    }

    /**
     * 커리큘럼 통계용 생성자
     */
    public CourseStatistics(Long courseNo, String courseName, Long curriculumCount, Integer maxWeekNumber) {
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.curriculumCount = curriculumCount;
        this.maxWeekNumber = maxWeekNumber;
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 수강률 계산 (소수점 2자리)
     */
    public BigDecimal getEnrollmentRatio() {
        if (enrollmentRatio != null) {
            return enrollmentRatio.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
     * 수강 가능 여부
     */
    public boolean isAvailable() {
        return currentStudents != null && maxStudents != null && currentStudents < maxStudents;
    }

    /**
     * 정원 대비 수강률 (0-100)
     */
    public BigDecimal getEnrollmentPercentage() {
        if (currentStudents != null && maxStudents != null && maxStudents > 0) {
            return BigDecimal.valueOf(currentStudents)
                .divide(BigDecimal.valueOf(maxStudents), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 승인률 계산
     */
    public BigDecimal getApprovalRate() {
        if (totalEnrollments != null && totalEnrollments > 0 && approvedEnrollments != null) {
            return BigDecimal.valueOf(approvedEnrollments)
                .divide(BigDecimal.valueOf(totalEnrollments), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 대기률 계산
     */
    public BigDecimal getPendingRate() {
        if (totalEnrollments != null && totalEnrollments > 0 && pendingEnrollments != null) {
            return BigDecimal.valueOf(pendingEnrollments)
                .divide(BigDecimal.valueOf(totalEnrollments), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 거절률 계산
     */
    public BigDecimal getRejectionRate() {
        if (totalEnrollments != null && totalEnrollments > 0 && rejectedEnrollments != null) {
            return BigDecimal.valueOf(rejectedEnrollments)
                .divide(BigDecimal.valueOf(totalEnrollments), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
