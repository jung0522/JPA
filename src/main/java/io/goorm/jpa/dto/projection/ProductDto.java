package io.goorm.jpa.dto.projection;

import io.goorm.jpa.enums.ProductStatus;

/**
 * Record/DTO Projection
 * 필요한 필드만 조회 (생성자 기반 매핑)
 */
public record ProductDto(
        Long productId,
        String productName,
        Integer price,
        ProductStatus status
) {
}
