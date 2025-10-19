package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.common.PageResponse;
import io.goorm.jpa.dto.course.CourseCreateRequest;
import io.goorm.jpa.dto.course.CourseResponse;
import io.goorm.jpa.dto.course.CourseUpdateRequest;
import io.goorm.jpa.dto.course.CourseSearchCondition;
import io.goorm.jpa.dto.curriculum.CurriculumResponse;
import io.goorm.jpa.dto.user.UserResponse;
import io.goorm.jpa.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Course API Controller
 * - RESTful API 설계
 * - JPQL 사용
 * - ManyToOne 단방향
 */
@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "강의 API")
public class CourseApiController {

    private final CourseService courseService;

    // ===== 기본 CRUD 엔드포인트 =====

    @GetMapping
    @Operation(summary = "강의 목록 조회")
    public ApiResponse<PageResponse<CourseResponse>> getList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> page = courseService.getList(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/search")
    @Operation(summary = "강의 검색 (RESTful)")
    public ApiResponse<PageResponse<CourseResponse>> searchCourses(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean include,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // SearchCondition DTO로 변환
        CourseSearchCondition.CourseSearchConditionBuilder builder = CourseSearchCondition.builder();
        
        if (searchField != null && keyword != null) {
            switch (searchField) {
                case "courseName" -> builder.courseName(keyword);
                case "instructorName" -> builder.instructorName(keyword);
                case "description" -> builder.description(keyword);
                case "all" -> {
                    // 전체 검색: 모든 필드에 동일한 키워드 설정
                    // Repository에서 OR 조건으로 처리
                    builder.courseName(keyword);
                    builder.instructorName(keyword);
                    builder.description(keyword);
                }
                default -> {
                    builder.courseName(keyword);
                    builder.instructorName(keyword);
                    builder.description(keyword);
                }
            }
        }
        
        CourseSearchCondition condition = builder.build();
        
        Page<CourseResponse> page = courseService.searchCourses(condition, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/my")
    @Operation(summary = "내 강의 목록 (강사)")
    public ApiResponse<PageResponse<CourseResponse>> getMyCourses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> page = courseService.getMyCourses(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    // ===== 통계 엔드포인트 =====

    @GetMapping("/stats/instructor")
    @Operation(summary = "강사별 강의 통계")
    public ApiResponse<List<Object[]>> getInstructorStatistics() {
        List<Object[]> statistics = courseService.getInstructorStatistics();
        return ApiResponse.success(statistics);
    }

    @GetMapping("/stats/monthly")
    @Operation(summary = "월별 강의 개설 통계")
    public ApiResponse<List<Object[]>> getMonthlyStatistics() {
        List<Object[]> statistics = courseService.getMonthlyStatistics();
        return ApiResponse.success(statistics);
    }

    // ===== Step 1: 팝업용 API 엔드포인트 =====

    @GetMapping("/{courseNo}/curriculums")
    @Operation(summary = "강의 커리큘럼 조회 (Step 1: 팝업용)")
    public ApiResponse<List<CurriculumResponse>> getCurriculums(@PathVariable Long courseNo) {
        List<CurriculumResponse> curriculums = courseService.getCurriculums(courseNo);
        return ApiResponse.success(curriculums);
    }

    @GetMapping("/{courseNo}/students")
    @Operation(summary = "강의 수강생 목록 조회 (Step 1: 팝업용)")
    public ApiResponse<List<UserResponse>> getEnrolledStudents(@PathVariable Long courseNo) {
        List<UserResponse> students = courseService.getEnrolledStudents(courseNo);
        return ApiResponse.success(students);
    }

    // ===== 강의 상세 조회 (URL 매핑 충돌 방지를 위해 마지막에 배치) =====
    
    @GetMapping("/{courseNo:[0-9]+}")
    @Operation(summary = "강의 상세 조회")
    public ApiResponse<CourseResponse> getDetail(@PathVariable Long courseNo) {
        CourseResponse response = courseService.getDetail(courseNo);
        return ApiResponse.success(response);
    }

    @PostMapping
    @Operation(summary = "강의 생성 (Step 2에서 구현)")
    public ApiResponse<CourseResponse> create(@Valid @RequestBody CourseCreateRequest request) {
        // Step 2에서 구현 예정
        throw new UnsupportedOperationException("강의 생성은 Step 2에서 구현 예정입니다.");
    }

    @PutMapping("/{courseNo}")
    @Operation(summary = "강의 수정 (강사 본인만)")
    public ApiResponse<CourseResponse> update(
            @PathVariable Long courseNo,
            @Valid @RequestBody CourseUpdateRequest request
    ) {
        CourseResponse response = courseService.update(courseNo, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{courseNo}")
    @Operation(summary = "강의 삭제 (Step 2에서 구현)")
    public ApiResponse<Void> delete(@PathVariable Long courseNo) {
        courseService.delete(courseNo);
        return ApiResponse.success();
    }
}