package io.goorm.jpa.dto.enrollment;

import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.enums.EnrollmentStatus;

import java.time.LocalDateTime;

/**
 * 수강신청 응답 DTO
 */
public record EnrollmentResponse(
        Long enrollmentNo,
        Long studentNo,
        String studentName,
        Long courseNo,
        String courseName,
        String instructorName,
        EnrollmentStatus status,
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
