package io.goorm.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * MyBatis 스타일 카테고리 모델
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel extends BaseModel {

    private Long categoryId;
    private String categoryName;
}
