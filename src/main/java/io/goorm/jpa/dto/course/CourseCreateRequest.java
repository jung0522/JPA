package io.goorm.jpa.dto.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 강의 생성 요청 DTO
 */
public record CourseCreateRequest(
        @NotBlank(message = "{course.name.notblank}")
        @Size(max = 200, message = "{course.name.size}")
        String name,

        @Size(max = 2000, message = "{course.description.size}")
        String description,

        @NotNull(message = "{course.maxStudents.notnull}")
        @Min(value = 1, message = "{course.maxStudents.min}")
        Integer maxStudents
) {
}
