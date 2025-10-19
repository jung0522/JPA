package io.goorm.jpa.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

/**
 * 인기 강의 조회 조건 DTO
 */
@Getter
@Setter
public class PopularCourseCondition {
    private String instructorName;
    private Long minStudentCount;
    private Long maxStudentCount;
    private Integer limit;
    
    public PopularCourseCondition() {
        this.limit = 5; // 기본값
    }
    
    public PopularCourseCondition(String instructorName, Long minStudentCount, Long maxStudentCount, Integer limit) {
        this.instructorName = instructorName;
        this.minStudentCount = minStudentCount;
        this.maxStudentCount = maxStudentCount;
        this.limit = limit != null ? limit : 5;
    }
    
    public static PopularCourseCondition of(Integer limit) {
        PopularCourseCondition condition = new PopularCourseCondition();
        condition.setLimit(limit);
        return condition;
    }
}
