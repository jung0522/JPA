package io.goorm.jpa.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * MyBatis Model 공통 필드
 * - Audit 정보 (생성/수정 시간, 생성자/수정자)
 */
@Data
public abstract class BaseModel {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
