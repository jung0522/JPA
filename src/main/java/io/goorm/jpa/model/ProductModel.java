package io.goorm.jpa.model;

import io.goorm.jpa.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MyBatis 스타일 모델 - 데이터베이스 테이블 구조 그대로 매핑
 * 패러다임: 데이터 중심 (FK를 Long으로 표현, JOIN 결과를 필드로 펼침)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel extends BaseModel {

    private Long productId;
    private String productName;
    private String description;
    private Integer price;
    private Integer stockQuantity;

    // FK를 그대로 노출 - 객체 참조가 아닌 ID 값
    private Long categoryId;

    // JOIN 결과를 필드로 펼침 (MyBatis 전형적 패턴)
    private String categoryName;

    private ProductStatus status;

    /**
     * MyBatis Mapper 예시:
     *
     * SELECT
     *     p.product_id,
     *     p.product_name,
     *     p.description,
     *     p.price,
     *     p.stock_quantity,
     *     p.category_id,
     *     c.category_name,          -- JOIN 결과
     *     p.status,
     *     p.created_at,
     *     p.updated_at,
     *     p.created_by,
     *     p.updated_by
     * FROM tb_product p
     * LEFT JOIN tb_category c ON p.category_id = c.category_id
     * WHERE p.product_id = #{productId}
     *
     * → 카테고리 정보를 조회하려면 개발자가 직접 JOIN 쿼리 작성
     * → 결과를 필드로 flat하게 펼쳐서 받음
     */
}
