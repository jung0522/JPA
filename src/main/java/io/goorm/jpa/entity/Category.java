package io.goorm.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카테고리 엔티티
 * - Self 참조 제거 (단순화)
 * - 단일 계층 구조
 */
@Entity
@Table(name = "tb_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    // === 생성 메서드 ===
    public static Category of(String categoryName) {
        Category category = new Category();
        category.categoryName = categoryName;
        return category;
    }
}
