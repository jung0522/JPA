package io.goorm.jpa.dto.course;

import io.goorm.jpa.entity.Course;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 강의 응답 DTO
 * Step 1: weeks는 빈 배열 (양방향 없음)
 */
public record CourseResponse(
        Long courseNo,
        String name,
        String description,
        Integer maxStudents,
        Integer currentStudents,
        Long instructorNo,
        String instructorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
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
