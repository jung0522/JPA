package io.goorm.jpa.dto;

/**
 * 상품 생성 요청 DTO
 */
public record ProductCreateRequest(
        String productName,
        String description,
        Integer price,
        Integer stockQuantity
) {
}
