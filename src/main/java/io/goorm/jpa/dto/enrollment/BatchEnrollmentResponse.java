package io.goorm.jpa.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수강신청 일괄 처리 응답 DTO
 * - 배치 처리 결과 정보
 * - 성공/실패 통계
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEnrollmentResponse {

    // ===== 기본 정보 =====
    private BatchEnrollmentRequest.BatchAction action; // 처리 액션
    private int totalCount; // 전체 처리 대상 수
    private int successCount; // 성공한 처리 수
    private int failureCount; // 실패한 처리 수
    private LocalDateTime processedAt; // 처리 완료 시간

    // ===== 상세 결과 =====
    private List<EnrollmentProcessResult> results; // 개별 처리 결과
    private List<String> errors; // 전체 에러 메시지

    // ===== 통계 정보 =====
    private double successRate; // 성공률 (%)
    private String processingTime; // 처리 소요 시간

    // ===== 개별 처리 결과 =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnrollmentProcessResult {
        private Long enrollmentId; // 수강신청 ID
        private boolean success; // 처리 성공 여부
        private String message; // 처리 결과 메시지
        private String error; // 에러 메시지 (실패 시)
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 성공률 계산
     */
    public double getSuccessRate() {
        if (totalCount == 0) return 0.0;
        return (double) successCount / totalCount * 100;
    }

    /**
     * 전체 성공 여부
     */
    public boolean isAllSuccess() {
        return failureCount == 0 && successCount > 0;
    }

    /**
     * 전체 실패 여부
     */
    public boolean isAllFailure() {
        return successCount == 0 && failureCount > 0;
    }

    /**
     * 부분 성공 여부
     */
    public boolean isPartialSuccess() {
        return successCount > 0 && failureCount > 0;
    }

    /**
     * 성공한 수강신청 ID 목록
     */
    public List<Long> getSuccessEnrollmentIds() {
        return results.stream()
            .filter(EnrollmentProcessResult::isSuccess)
            .map(EnrollmentProcessResult::getEnrollmentId)
            .toList();
    }

    /**
     * 실패한 수강신청 ID 목록
     */
    public List<Long> getFailureEnrollmentIds() {
        return results.stream()
            .filter(result -> !result.isSuccess())
            .map(EnrollmentProcessResult::getEnrollmentId)
            .toList();
    }

    /**
     * 에러 메시지 목록
     */
    public List<String> getErrorMessages() {
        return results.stream()
            .filter(result -> !result.isSuccess())
            .map(EnrollmentProcessResult::getError)
            .filter(error -> error != null && !error.trim().isEmpty())
            .toList();
    }

    /**
     * 빌더 패턴을 위한 정적 팩토리 메서드
     */
    public static BatchEnrollmentResponseBuilder builder() {
        return new BatchEnrollmentResponseBuilder();
    }

    /**
     * 성공 응답 생성
     */
    public static BatchEnrollmentResponse success(BatchEnrollmentRequest.BatchAction action, 
                                                 int totalCount, 
                                                 List<EnrollmentProcessResult> results) {
        int successCount = (int) results.stream().filter(EnrollmentProcessResult::isSuccess).count();
        int failureCount = totalCount - successCount;
        
        return BatchEnrollmentResponse.builder()
            .action(action)
            .totalCount(totalCount)
            .successCount(successCount)
            .failureCount(failureCount)
            .results(results)
            .processedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 실패 응답 생성
     */
    public static BatchEnrollmentResponse failure(BatchEnrollmentRequest.BatchAction action, 
                                                 int totalCount, 
                                                 List<String> errors) {
        return BatchEnrollmentResponse.builder()
            .action(action)
            .totalCount(totalCount)
            .successCount(0)
            .failureCount(totalCount)
            .errors(errors)
            .processedAt(LocalDateTime.now())
            .build();
    }
}
