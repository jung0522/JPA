package io.goorm.jpa.dto.dashboard;

import io.goorm.jpa.dto.board.BoardResponse;

import java.util.List;

/**
 * 대시보드 응답 DTO
 */
public record DashboardResponse(
        Statistics statistics,
        List<BoardResponse> recentBoards,
        List<PopularCourse> popularCourses  // Step 2 예정
) {
    public record Statistics(
            Long totalBoards,
            Long totalCourses,
            Long totalEnrollments
    ) {}

    public record PopularCourse(
            Long courseNo,
            String courseName,
            String instructorName,
            Integer studentCount
    ) {}
}
