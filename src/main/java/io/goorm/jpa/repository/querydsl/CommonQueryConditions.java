package io.goorm.jpa.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import io.goorm.jpa.enums.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QueryDSL 공통 조건 모듈
 * - 재사용 가능한 동적 조건 메서드들
 * - 타입 안전성 보장
 * - 일관된 조건 처리
 */
public class CommonQueryConditions {

    // ===== 문자열 조건 =====

    /**
     * 문자열 포함 검색 (LIKE)
     */
    public static BooleanExpression stringContains(StringPath path, String value) {
        return value != null && !value.trim().isEmpty() 
            ? path.contains(value.trim()) : null;
    }

    /**
     * 문자열 포함 검색 (LIKE) - include 옵션
     */
    public static BooleanExpression stringContains(StringPath path, String value, Boolean include) {
        if (value == null || value.trim().isEmpty()) return null;
        return include != null && include 
            ? path.contains(value.trim()) 
            : path.eq(value.trim());
    }

    /**
     * 문자열 정확히 일치 (EQ)
     */
    public static BooleanExpression stringEq(StringPath path, String value) {
        return value != null && !value.trim().isEmpty() 
            ? path.eq(value.trim()) : null;
    }

    /**
     * 문자열로 시작 (STARTS_WITH)
     */
    public static BooleanExpression stringStartsWith(StringPath path, String value) {
        return value != null && !value.trim().isEmpty() 
            ? path.startsWith(value.trim()) : null;
    }

    /**
     * 문자열로 끝남 (ENDS_WITH)
     */
    public static BooleanExpression stringEndsWith(StringPath path, String value) {
        return value != null && !value.trim().isEmpty() 
            ? path.endsWith(value.trim()) : null;
    }

    // ===== 숫자 조건 =====

    /**
     * 숫자 범위 검색 (BETWEEN)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression numberBetween(
            NumberPath<T> path, T minValue, T maxValue) {
        if (minValue != null && maxValue != null) {
            return path.between(minValue, maxValue);
        } else if (minValue != null) {
            return path.goe(minValue);
        } else if (maxValue != null) {
            return path.loe(maxValue);
        }
        return null;
    }

    /**
     * 숫자 이상 (GREATER_THAN_OR_EQUAL)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression numberGoe(
            NumberPath<T> path, T value) {
        return value != null ? path.goe(value) : null;
    }

    /**
     * 숫자 이하 (LESS_THAN_OR_EQUAL)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression numberLoe(
            NumberPath<T> path, T value) {
        return value != null ? path.loe(value) : null;
    }

    /**
     * 숫자 초과 (GREATER_THAN)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression numberGt(
            NumberPath<T> path, T value) {
        return value != null ? path.gt(value) : null;
    }

    /**
     * 숫자 미만 (LESS_THAN)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression numberLt(
            NumberPath<T> path, T value) {
        return value != null ? path.lt(value) : null;
    }

    // ===== 날짜 조건 =====

    /**
     * 날짜 범위 검색 (BETWEEN)
     */
    public static BooleanExpression dateBetween(
            DateTimePath<LocalDateTime> path, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return path.between(startDate, endDate);
        } else if (startDate != null) {
            return path.goe(startDate);
        } else if (endDate != null) {
            return path.loe(endDate);
        }
        return null;
    }

    /**
     * 날짜 이후 (AFTER)
     */
    public static BooleanExpression dateAfter(
            DateTimePath<LocalDateTime> path, LocalDateTime date) {
        return date != null ? path.goe(date) : null;
    }

    /**
     * 날짜 이전 (BEFORE)
     */
    public static BooleanExpression dateBefore(
            DateTimePath<LocalDateTime> path, LocalDateTime date) {
        return date != null ? path.loe(date) : null;
    }

    // ===== 열거형 조건 =====

    /**
     * 열거형 일치 (EQ)
     */
    public static <T extends Enum<T>> BooleanExpression enumEq(
            com.querydsl.core.types.dsl.EnumPath<T> path, T value) {
        return value != null ? path.eq(value) : null;
    }

    /**
     * 열거형 목록 포함 (IN)
     */
    public static <T extends Enum<T>> BooleanExpression enumIn(
            com.querydsl.core.types.dsl.EnumPath<T> path, List<T> values) {
        return values != null && !values.isEmpty() ? path.in(values) : null;
    }

    // ===== 불린 조건 =====

    /**
     * 불린 값 일치 (EQ)
     */
    public static BooleanExpression booleanEq(
            com.querydsl.core.types.dsl.BooleanPath path, Boolean value) {
        return value != null ? path.eq(value) : null;
    }

    /**
     * 불린 값이 true (IS_TRUE)
     */
    public static BooleanExpression booleanIsTrue(
            com.querydsl.core.types.dsl.BooleanPath path, Boolean value) {
        return Boolean.TRUE.equals(value) ? path.isTrue() : null;
    }

    /**
     * 불린 값이 false (IS_FALSE)
     */
    public static BooleanExpression booleanIsFalse(
            com.querydsl.core.types.dsl.BooleanPath path, Boolean value) {
        return Boolean.FALSE.equals(value) ? path.isFalse() : null;
    }

    // ===== 복합 조건 =====

    /**
     * 삭제되지 않은 레코드 (NOT_DELETED)
     */
    public static BooleanExpression notDeleted(
            com.querydsl.core.types.dsl.BooleanPath deletedPath) {
        return deletedPath.eq(false);
    }

    /**
     * 활성 상태 (ACTIVE)
     */
    public static BooleanExpression isActive(
            com.querydsl.core.types.dsl.BooleanPath activePath) {
        return activePath.eq(true);
    }

    /**
     * 비활성 상태 (INACTIVE)
     */
    public static BooleanExpression isInactive(
            com.querydsl.core.types.dsl.BooleanPath activePath) {
        return activePath.eq(false);
    }

    // ===== 비율/퍼센트 조건 =====

    /**
     * 비율 범위 검색 (두 숫자 필드의 비율)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression ratioBetween(
            NumberPath<T> numerator, NumberPath<T> denominator, 
            Double minRatio, Double maxRatio) {
        if (minRatio == null && maxRatio == null) return null;
        
        BooleanExpression denominatorGtZero = denominator.gt(0);
        
        if (minRatio != null && maxRatio != null) {
            return denominatorGtZero
                .and(numerator.divide(denominator).between(minRatio, maxRatio));
        } else if (minRatio != null) {
            return denominatorGtZero
                .and(numerator.divide(denominator).goe(minRatio));
        } else {
            return denominatorGtZero
                .and(numerator.divide(denominator).loe(maxRatio));
        }
    }

    /**
     * 비율 이상 (두 숫자 필드의 비율)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression ratioGoe(
            NumberPath<T> numerator, NumberPath<T> denominator, Double minRatio) {
        if (minRatio == null) return null;
        return denominator.gt(0)
            .and(numerator.divide(denominator).goe(minRatio));
    }

    /**
     * 비율 이하 (두 숫자 필드의 비율)
     */
    public static <T extends Number & Comparable<T>> BooleanExpression ratioLoe(
            NumberPath<T> numerator, NumberPath<T> denominator, Double maxRatio) {
        if (maxRatio == null) return null;
        return denominator.gt(0)
            .and(numerator.divide(denominator).loe(maxRatio));
    }

    // ===== NULL 체크 조건 =====

    /**
     * NULL이 아님 (IS_NOT_NULL)
     */
    public static <T> BooleanExpression isNotNull(
            com.querydsl.core.types.dsl.SimpleExpression<T> path) {
        return path.isNotNull();
    }

    /**
     * NULL임 (IS_NULL)
     */
    public static <T> BooleanExpression isNull(
            com.querydsl.core.types.dsl.SimpleExpression<T> path) {
        return path.isNull();
    }

    // ===== 특수 조건 =====

    /**
     * 빈 문자열이 아님 (NOT_EMPTY)
     */
    public static BooleanExpression isNotEmpty(StringPath path) {
        return path.isNotEmpty();
    }

    /**
     * 빈 문자열임 (IS_EMPTY)
     */
    public static BooleanExpression isEmpty(StringPath path) {
        return path.isEmpty();
    }

    /**
     * 길이 범위 (LENGTH_BETWEEN)
     */
    public static BooleanExpression lengthBetween(
            StringPath path, Integer minLength, Integer maxLength) {
        if (minLength != null && maxLength != null) {
            return path.length().between(minLength, maxLength);
        } else if (minLength != null) {
            return path.length().goe(minLength);
        } else if (maxLength != null) {
            return path.length().loe(maxLength);
        }
        return null;
    }
}
