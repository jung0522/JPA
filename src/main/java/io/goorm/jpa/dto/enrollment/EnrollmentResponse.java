package io.goorm.jpa.dto.enrollment;

import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 수강신청 응답 DTO
 */
public record EnrollmentResponse(
        @Schema(description = "수강신청 번호", example = "1")
        Long enrollmentNo,
        
        @Schema(description = "학생 번호", example = "3")
        Long studentNo,
        
        @Schema(description = "학생명", example = "홍길동")
        String studentName,
        
        @Schema(description = "강의 번호", example = "1")
        Long courseNo,
        
        @Schema(description = "강의명", example = "Spring Boot 기초")
        String courseName,
        
        @Schema(description = "강사명", example = "김강사")
        String instructorName,
        
        @Schema(description = "수강신청 상태", example = "PENDING")
        EnrollmentStatus status,
        
        @Schema(description = "신청일시", example = "2024-01-01T09:00:00")
        LocalDateTime createdAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getEnrollmentNo(),
                enrollment.getStudent().getUserNo(),
                enrollment.getStudent().getFullName(),
                enrollment.getCourse().getCourseNo(),
                enrollment.getCourse().getName(),
                enrollment.getCourse().getInstructor().getFullName(),
                enrollment.getStatus(),
                enrollment.getCreatedAt()
        );
    }
}
