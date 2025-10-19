package io.goorm.jpa.dto.course;

import io.goorm.jpa.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 강의 응답 DTO
 * Step 1: weeks는 빈 배열 (양방향 없음)
 */
public record CourseResponse(
        @Schema(description = "강의 번호", example = "1")
        Long courseNo,
        
        @Schema(description = "강의명", example = "Spring Boot 기초")
        String name,
        
        @Schema(description = "강의 설명", example = "Spring Boot를 이용한 웹 애플리케이션 개발")
        String description,
        
        @Schema(description = "최대 수강생 수", example = "30")
        Integer maxStudents,
        
        @Schema(description = "현재 수강생 수", example = "15")
        Integer currentStudents,
        
        @Schema(description = "강사 번호", example = "2")
        Long instructorNo,
        
        @Schema(description = "강사명", example = "김강사")
        String instructorName,
        
        @Schema(description = "생성일시", example = "2024-01-01T09:00:00")
        LocalDateTime createdAt,
        
        @Schema(description = "수정일시", example = "2024-01-01T09:00:00")
        LocalDateTime updatedAt,
        
        @Schema(description = "강의 주차 목록 (Step 1: 빈 배열)", example = "[]")
        List<Object> weeks  // Step 1: 빈 배열, Step 2: CourseWeekResponse
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getCourseNo(),
                course.getName(),
                course.getDescription(),
                course.getMaxStudents(),
                course.getCurrentStudents(),
                course.getInstructor().getUserNo(),
                course.getInstructor().getFullName(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                Collections.emptyList()  // Step 1: 빈 배열
        );
    }
}
