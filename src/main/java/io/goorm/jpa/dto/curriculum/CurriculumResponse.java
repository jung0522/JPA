package io.goorm.jpa.dto.curriculum;

import io.goorm.jpa.entity.Curriculum;
import lombok.Builder;

/**
 * 커리큘럼 응답 DTO
 */
@Builder
public record CurriculumResponse(
    Long curriculumNo,
    Integer weekNumber,
    String title,
    String description,
    String materials,
    Integer duration
) {
    public static CurriculumResponse from(Curriculum curriculum) {
        return CurriculumResponse.builder()
            .curriculumNo(curriculum.getCurriculumNo())
            .weekNumber(curriculum.getWeekNumber())
            .title(curriculum.getTitle())
            .description(curriculum.getDescription())
            .materials(curriculum.getMaterials())
            .duration(curriculum.getDuration())
            .build();
    }
}
