package io.goorm.jpa.dto.dashboard;

/**
 * 인기 강의 DTO
 */
public record PopularCourse(
    Long courseNo,
    String courseName,
    String instructorName,
    Long studentCount
) {}
