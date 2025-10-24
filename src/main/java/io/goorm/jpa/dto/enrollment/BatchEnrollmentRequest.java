package io.goorm.jpa.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 수강신청 일괄 처리 요청 DTO
 * - 여러 수강신청을 한 번에 승인/거절
 * - 배치 처리용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEnrollmentRequest {

    // ===== 기본 정보 =====
    private List<Long> enrollmentIds; // 처리할 수강신청 ID 목록
    private BatchAction action; // 일괄 처리 액션 (APPROVE, REJECT)
    private String reason; // 처리 사유 (선택사항)

    // ===== 액션 타입 =====
    public enum BatchAction {
        APPROVE, // 일괄 승인
        REJECT   // 일괄 거절
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 승인 액션인지 확인
     */
    public boolean isApprove() {
        return BatchAction.APPROVE.equals(action);
    }

    /**
     * 거절 액션인지 확인
     */
    public boolean isReject() {
        return BatchAction.REJECT.equals(action);
    }

    /**
     * 처리할 수강신청이 있는지 확인
     */
    public boolean hasEnrollments() {
        return enrollmentIds != null && !enrollmentIds.isEmpty();
    }

    /**
     * 처리할 수강신청 개수
     */
    public int getEnrollmentCount() {
        return enrollmentIds != null ? enrollmentIds.size() : 0;
    }

    /**
     * 빌더 패턴을 위한 정적 팩토리 메서드
     */
    public static BatchEnrollmentRequestBuilder builder() {
        return new BatchEnrollmentRequestBuilder();
    }

    /**
     * 승인 요청 생성
     */
    public static BatchEnrollmentRequest approve(List<Long> enrollmentIds) {
        return BatchEnrollmentRequest.builder()
            .enrollmentIds(enrollmentIds)
            .action(BatchAction.APPROVE)
            .build();
    }

    /**
     * 승인 요청 생성 (사유 포함)
     */
    public static BatchEnrollmentRequest approve(List<Long> enrollmentIds, String reason) {
        return BatchEnrollmentRequest.builder()
            .enrollmentIds(enrollmentIds)
            .action(BatchAction.APPROVE)
            .reason(reason)
            .build();
    }

    /**
     * 거절 요청 생성
     */
    public static BatchEnrollmentRequest reject(List<Long> enrollmentIds) {
        return BatchEnrollmentRequest.builder()
            .enrollmentIds(enrollmentIds)
            .action(BatchAction.REJECT)
            .build();
    }

    /**
     * 거절 요청 생성 (사유 포함)
     */
    public static BatchEnrollmentRequest reject(List<Long> enrollmentIds, String reason) {
        return BatchEnrollmentRequest.builder()
            .enrollmentIds(enrollmentIds)
            .action(BatchAction.REJECT)
            .reason(reason)
            .build();
    }
}
