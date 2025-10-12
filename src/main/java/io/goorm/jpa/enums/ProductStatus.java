package io.goorm.jpa.enums;

/**
 * 상품 상태 Enum
 * Model과 Entity에서 공통으로 사용
 */
public enum ProductStatus {
    ACTIVE,         // 판매중
    SOLD_OUT,       // 품절
    DISCONTINUED    // 단종
}
