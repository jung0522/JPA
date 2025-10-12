package io.goorm.jpa.entity;

import io.goorm.jpa.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA 엔티티 - 객체 지향적 설계
 * 패러다임: 객체 중심 (FK가 아닌 객체 참조, Audit 자동화)
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

    /**
     * ⭐ 핵심 차이점: FK가 아닌 객체 참조!
     *
     * ProductModel (MyBatis):
     *   - Long categoryId (FK)
     *   - String categoryName (JOIN 결과)
     *   → 개발자가 직접 JOIN 쿼리 작성
     *
     * Product Entity (JPA):
     *   - Category category (객체 참조)
     *   → product.getCategory().getCategoryName() 형태로 접근 가능
     *   → 객체 그래프 탐색!
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    // === 생성 메서드 ===
    public static Product of(String productName, String description, Integer price,
                            Integer stockQuantity, Category category) {
        Product product = new Product();
        product.productName = productName;
        product.description = description;
        product.price = price;
        product.stockQuantity = stockQuantity;
        product.category = category;
        product.status = ProductStatus.ACTIVE;
        return product;
    }

    // === 비즈니스 메서드 ===
    public void changePrice(Integer newPrice) {
        if (newPrice < 0) {
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

    /**
     * 객체 그래프 탐색 예시:
     * product.getCategory().getCategoryName()
     *
     * MyBatis였다면?
     * 1. SELECT category_id FROM tb_product WHERE product_id = ?
     * 2. SELECT category_name FROM tb_category WHERE category_id = ?
     * → 개발자가 직접 여러 쿼리를 작성하거나 JOIN 쿼리 작성
     *
     * JPA는?
     * → product.getCategory().getCategoryName() (끝!)
     * → JPA가 알아서 필요 시 JOIN 또는 추가 쿼리 실행
     */
}
