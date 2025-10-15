package io.goorm.jpa.m2m.dto;

import java.util.List;

public record PostCreateRequest(
    String title,
    String content,
    List<String> tagNames
) {}
