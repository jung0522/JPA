package io.goorm.jpa.m2m.dto;

public record PostWithTagsDto(
    Long id,
    String title,
    String tags
) {}
