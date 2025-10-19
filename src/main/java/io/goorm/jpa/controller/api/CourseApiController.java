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
 * - JPQL 사용
 * - ManyToOne 단방향
 * - Step 1: 기본 CRUD + 검색
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
    @Operation(summary = "강의 검색")
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



    // ===== Step 1: 팝업용 API 엔드포인트 =====


    @GetMapping("/{courseNo}/students")
    @Operation(summary = "강의 수강생 목록 조회")
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

}