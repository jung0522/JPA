package io.goorm.jpa.dto.enrollment;

import jakarta.validation.constraints.NotNull;

/**
 * 수강신청 생성 요청 DTO
 */
public record EnrollmentCreateRequest(
        @NotNull(message = "{enrollment.courseNo.notnull}")
        Long courseNo
) {
}
