package io.goorm.jpa.m2m.dto;

public record TagCountDto(
    Long id,
    String name,
    Long count
) {}
