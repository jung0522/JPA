package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.common.PageResponse;
import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.service.EnrollmentService;
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
 * Enrollment API Controller
 * - QueryDSL 사용
 * - Optimistic Lock
 */
@Slf4j
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "수강신청 API")
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "수강신청")
    public ApiResponse<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentCreateRequest request) {
        EnrollmentResponse response = enrollmentService.enroll(request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{enrollmentNo}")
    @Operation(summary = "수강신청 취소")
    public ApiResponse<Void> cancel(@PathVariable Long enrollmentNo) {
        enrollmentService.cancel(enrollmentNo);
        return ApiResponse.success();
    }

    @PutMapping("/{enrollmentNo}/approve")
    @Operation(summary = "수강신청 승인 (관리자)")
    public ApiResponse<EnrollmentResponse> approve(@PathVariable Long enrollmentNo) {
        EnrollmentResponse response = enrollmentService.approve(enrollmentNo);
        return ApiResponse.success(response);
    }

    @PutMapping("/{enrollmentNo}/reject")
    @Operation(summary = "수강신청 거절 (관리자)")
    public ApiResponse<EnrollmentResponse> reject(@PathVariable Long enrollmentNo) {
        EnrollmentResponse response = enrollmentService.reject(enrollmentNo);
        return ApiResponse.success(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 수강신청 목록")
    public ApiResponse<PageResponse<EnrollmentResponse>> getMyEnrollments(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<EnrollmentResponse> page = enrollmentService.getMyEnrollments(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping
    @Operation(summary = "수강신청 관리 (관리자, 동적 검색)")
    public ApiResponse<PageResponse<EnrollmentResponse>> search(
            @RequestParam(required = false) Long studentNo,
            @RequestParam(required = false) Long courseNo,
            @RequestParam(required = false) EnrollmentStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<EnrollmentResponse> page = enrollmentService.search(studentNo, courseNo, status, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }
}
