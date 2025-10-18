package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.common.PageResponse;
import io.goorm.jpa.dto.course.CourseCreateRequest;
import io.goorm.jpa.dto.course.CourseResponse;
import io.goorm.jpa.dto.course.CourseUpdateRequest;
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

/**
 * Course API Controller
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

    @PostMapping
    @Operation(summary = "강의 생성 (강사만)")
    public ApiResponse<CourseResponse> create(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.create(request);
        return ApiResponse.ok(response);
    }

    @GetMapping
    @Operation(summary = "강의 목록 조회")
    public ApiResponse<PageResponse<CourseResponse>> getList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> page = courseService.getList(pageable);
        return ApiResponse.ok(PageResponse.of(page));
    }

    @GetMapping("/available")
    @Operation(summary = "수강 가능한 강의 검색")
    public ApiResponse<PageResponse<CourseResponse>> searchAvailable(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> page = courseService.searchAvailableCourses(keyword, pageable);
        return ApiResponse.ok(PageResponse.of(page));
    }

    @GetMapping("/{courseNo}")
    @Operation(summary = "강의 상세 조회")
    public ApiResponse<CourseResponse> getDetail(@PathVariable Long courseNo) {
        CourseResponse response = courseService.getDetail(courseNo);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{courseNo}")
    @Operation(summary = "강의 수정 (강사 본인만)")
    public ApiResponse<CourseResponse> update(
            @PathVariable Long courseNo,
            @Valid @RequestBody CourseUpdateRequest request
    ) {
        CourseResponse response = courseService.update(courseNo, request);
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/{courseNo}")
    @Operation(summary = "강의 삭제 (Step 2에서 구현)")
    public ApiResponse<Void> delete(@PathVariable Long courseNo) {
        courseService.delete(courseNo);
        return ApiResponse.ok();
    }

    @GetMapping("/my")
    @Operation(summary = "내 강의 목록 (강사)")
    public ApiResponse<PageResponse<CourseResponse>> getMyCourses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CourseResponse> page = courseService.getMyCourses(pageable);
        return ApiResponse.ok(PageResponse.of(page));
    }
}
