package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.common.PageResponse;
import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentRequest;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentResponse;
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
 * - Step 1: 기본 CRUD + 동적 검색
 */
@Slf4j
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "수강신청 관리 API")
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
    @Operation(summary = "수강신청 승인 (관리자, 강사)")
    public ApiResponse<EnrollmentResponse> approve(@PathVariable Long enrollmentNo) {
        EnrollmentResponse response = enrollmentService.approve(enrollmentNo);
        return ApiResponse.success(response);
    }

    @PutMapping("/{enrollmentNo}/reject")
    @Operation(summary = "수강신청 거절 (관리자, 강사)")
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
    @Operation(summary = "수강신청 목록 조회 (개선)", description = "관리자용 동적 검색 - 상태별, 학생별 필터링 지원")
    public ApiResponse<PageResponse<EnrollmentResponse>> search(
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean include,
            @RequestParam(required = false) EnrollmentStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<EnrollmentResponse> page = enrollmentService.searchByAdminConditions(searchField, keyword, include, status, pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    // ===== Step 2-4: 배치 처리 API =====

    @PostMapping("/batch")
    @Operation(summary = "수강신청 일괄 처리 (신규)", description = "여러 수강신청을 한번에 승인/거절 - API only")
    public ApiResponse<BatchEnrollmentResponse> batchProcess(@Valid @RequestBody BatchEnrollmentRequest request) {
        BatchEnrollmentResponse response = enrollmentService.batchProcessEnrollments(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/batch/course/{courseNo}")
    @Operation(summary = "강의별 수강신청 일괄 처리 (신규)", description = "특정 강의의 모든 수강신청 일괄 처리 - API only")
    public ApiResponse<BatchEnrollmentResponse> batchProcessByCourse(
            @PathVariable Long courseNo,
            @RequestParam BatchEnrollmentRequest.BatchAction action,
            @RequestParam(required = false) String reason
    ) {
        BatchEnrollmentResponse response = enrollmentService.batchProcessEnrollmentsByCourse(courseNo, action, reason);
        return ApiResponse.success(response);
    }
}
