package io.goorm.jpa.dto.projection;

import io.goorm.jpa.enums.ProductStatus;

/**
 * Interface Projection
 * 필요한 필드만 조회 (Getter 메서드 정의)
 */
public interface ProductSummary {
    Long getProductId();
    String getProductName();
    Integer getPrice();
    ProductStatus getStatus();
}
