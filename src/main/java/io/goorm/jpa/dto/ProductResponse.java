package io.goorm.jpa.dto;

import io.goorm.jpa.entity.Product;
import io.goorm.jpa.enums.ProductStatus;

import java.time.LocalDateTime;

/**
 * 상품 응답 DTO
 */
public record ProductResponse(
        Long productId,
        String productName,
        String description,
        Integer price,
        Integer stockQuantity,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getCreatedBy(),
                product.getUpdatedBy()
        );
    }
}
