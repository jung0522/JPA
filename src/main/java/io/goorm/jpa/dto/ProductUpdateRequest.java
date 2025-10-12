package io.goorm.jpa.dto;

/**
 * 상품 수정 요청 DTO
 */
public record ProductUpdateRequest(
        String productName,
        String description,
        Integer price,
        Integer stockQuantity
) {
}
