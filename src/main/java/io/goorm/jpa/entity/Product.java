package io.goorm.jpa.entity;

import io.goorm.jpa.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 엔티티 - 단일 테이블 (연관관계 없음)
 */
@Entity
@Table(name = "tb_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    private Integer stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    // === 생성 메서드 ===
    public static Product of(String productName, String description, Integer price, Integer stockQuantity) {
        Product product = new Product();
        product.productName = productName;
        product.description = description;
        product.price = price;
        product.stockQuantity = stockQuantity;
        product.status = ProductStatus.ACTIVE;
        return product;
    }

    // === 비즈니스 메서드 ===
    public void update(String productName, String description, Integer price, Integer stockQuantity) {
        if (productName != null && !productName.isBlank()) {
            this.productName = productName;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            changePrice(price);
        }
        if (stockQuantity != null) {
            this.stockQuantity = stockQuantity;
        }
    }

    public void changePrice(Integer newPrice) {
        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        this.price = newPrice;
    }

    public void addStock(Integer quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(Integer quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity = restStock;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }
}
