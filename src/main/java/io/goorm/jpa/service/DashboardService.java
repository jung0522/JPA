package io.goorm.jpa.service;

import io.goorm.jpa.dto.board.BoardResponse;
import io.goorm.jpa.dto.dashboard.DashboardResponse;
import io.goorm.jpa.entity.Board;
import io.goorm.jpa.repository.BoardRepository;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 대시보드 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final BoardRepository boardRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 대시보드 데이터 조회
     */
    public DashboardResponse getDashboard() {
        // 통계
        Long totalBoards = boardRepository.countByDeletedFalse();
        Long totalCourses = courseRepository.countByDeletedFalse();
        Long totalEnrollments = enrollmentRepository.countByDeletedFalse();

        DashboardResponse.Statistics statistics = new DashboardResponse.Statistics(
                totalBoards,
                totalCourses,
                totalEnrollments
        );

        // 최근 게시글 5개
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Board> recentBoards = boardRepository.findByDeletedFalse(pageRequest).getContent();
        List<BoardResponse> recentBoardResponses = recentBoards.stream()
                .map(BoardResponse::from)
                .toList();

        // Step 2 예정: 인기 강의 (예제 데이터)
        List<DashboardResponse.PopularCourse> popularCourses = List.of(
                new DashboardResponse.PopularCourse(1L, "JPA 기초부터 실전까지", "김강사", 25),
                new DashboardResponse.PopularCourse(2L, "Spring Boot 완벽 가이드", "김강사", 20),
                new DashboardResponse.PopularCourse(3L, "QueryDSL 마스터하기", "김강사", 18),
                new DashboardResponse.PopularCourse(4L, "데이터베이스 설계와 최적화", "김강사", 15),
                new DashboardResponse.PopularCourse(5L, "RESTful API 설계", "김강사", 12)
        );

        return new DashboardResponse(statistics, recentBoardResponses, popularCourses);
    }
}
