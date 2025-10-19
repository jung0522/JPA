package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Course Repository
 * JPQL 사용
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 수강 가능한 강의 검색 (JPQL + Fetch Join)
     * - 이름 검색
     * - 정원 미달만
     * - 강사 정보 포함
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND (:keyword IS NULL OR c.name LIKE %:keyword%)
        AND c.currentStudents < c.maxStudents
        ORDER BY c.createdAt DESC
        """)
    Page<Course> searchAvailableCourses(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 전체 강의 목록 (Fetch Join)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findAllWithInstructor(Pageable pageable);

    /**
     * 강의 상세 조회 (Fetch Join)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.courseNo = :courseNo
        AND c.deleted = false
        """)
    Course findByIdWithInstructor(@Param("courseNo") Long courseNo);

    /**
     * 강사별 강의 목록
     */
    @Query("""
        SELECT c FROM Course c
        WHERE c.instructor = :instructor
        AND c.deleted = false
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findByInstructor(@Param("instructor") User instructor, Pageable pageable);

    /**
     * 삭제되지 않은 강의 수
     */
    Long countByDeletedFalse();

    // ===== 복잡한 검색 조건들 (JPQL) =====

    /**
     * 강의명과 강사명으로 통합 검색
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        WHERE c.deleted = false
        AND (:courseName IS NULL OR c.name LIKE %:courseName%)
        AND (:instructorName IS NULL OR i.fullName LIKE %:instructorName%)
        ORDER BY c.createdAt DESC
        """)
    Page<Course> searchByCourseAndInstructor(@Param("courseName") String courseName, 
                                            @Param("instructorName") String instructorName, 
                                            Pageable pageable);

    /**
     * 정원 범위로 검색
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND c.maxStudents BETWEEN :minCapacity AND :maxCapacity
        ORDER BY c.maxStudents DESC
        """)
    Page<Course> findByCapacityRange(@Param("minCapacity") Integer minCapacity, 
                                    @Param("maxCapacity") Integer maxCapacity, 
                                    Pageable pageable);

    /**
     * 수강 가능한 강의만 검색 (정원 미달)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND c.currentStudents < c.maxStudents
        AND (:keyword IS NULL OR c.name LIKE %:keyword% OR c.description LIKE %:keyword%)
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findAvailableCourses(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 강의 검색 (수강생 수 기준)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND c.currentStudents >= :minStudents
        ORDER BY c.currentStudents DESC, c.createdAt DESC
        """)
    Page<Course> findPopularCourses(@Param("minStudents") Integer minStudents, Pageable pageable);

    /**
     * 최근 개설된 강의 검색
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND c.createdAt >= :startDate
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findRecentCourses(@Param("startDate") java.time.LocalDateTime startDate, Pageable pageable);

    /**
     * 강의 상태별 검색 (수강 가능/마감)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND (:isAvailable IS NULL OR 
             (:isAvailable = true AND c.currentStudents < c.maxStudents) OR
             (:isAvailable = false AND c.currentStudents >= c.maxStudents))
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findByAvailability(@Param("isAvailable") Boolean isAvailable, Pageable pageable);


    /**
     * 정원 대비 수강률이 높은 강의
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        WHERE c.deleted = false
        AND c.maxStudents > 0
        AND (CAST(c.currentStudents AS FLOAT) / c.maxStudents) >= :minRatio
        ORDER BY (CAST(c.currentStudents AS FLOAT) / c.maxStudents) DESC
        """)
    Page<Course> findHighEnrollmentCourses(@Param("minRatio") Double minRatio, Pageable pageable);

    /**
     * 복합 조건 검색 (강의명, 강사명, 정원, 수강 가능 여부)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        WHERE c.deleted = false
        AND (:courseName IS NULL OR c.name LIKE %:courseName%)
        AND (:instructorName IS NULL OR i.fullName LIKE %:instructorName%)
        AND (:minCapacity IS NULL OR c.maxStudents >= :minCapacity)
        AND (:maxCapacity IS NULL OR c.maxStudents <= :maxCapacity)
        AND (:isAvailable IS NULL OR 
             (:isAvailable = true AND c.currentStudents < c.maxStudents) OR
             (:isAvailable = false AND c.currentStudents >= c.maxStudents))
        ORDER BY c.createdAt DESC
        """)
    Page<Course> findCoursesByComplexConditions(@Param("courseName") String courseName,
                                               @Param("instructorName") String instructorName,
                                               @Param("minCapacity") Integer minCapacity,
                                               @Param("maxCapacity") Integer maxCapacity,
                                               @Param("isAvailable") Boolean isAvailable,
                                               Pageable pageable);

    // ===== 동적 조건 검색 (JPQL의 진가) =====

    /**
     * 완전 동적 검색 - 모든 조건이 선택적
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        WHERE c.deleted = false
        AND (
            (:courseName IS NULL OR c.name LIKE %:courseName%)
            AND (:instructorName IS NULL OR i.fullName LIKE %:instructorName%)
            AND (:description IS NULL OR c.description LIKE %:description%)
        )
        OR (
            :courseName IS NOT NULL AND :instructorName IS NOT NULL AND :description IS NOT NULL
            AND :courseName = :instructorName AND :instructorName = :description
            AND (c.name LIKE %:courseName% OR i.fullName LIKE %:instructorName% OR c.description LIKE %:description%)
        )
        AND (:minCapacity IS NULL OR c.maxStudents >= :minCapacity)
        AND (:maxCapacity IS NULL OR c.maxStudents <= :maxCapacity)
        AND (:minCurrentStudents IS NULL OR c.currentStudents >= :minCurrentStudents)
        AND (:maxCurrentStudents IS NULL OR c.currentStudents <= :maxCurrentStudents)
        AND (:isAvailable IS NULL OR 
             (:isAvailable = true AND c.currentStudents < c.maxStudents) OR
             (:isAvailable = false AND c.currentStudents >= c.maxStudents))
        AND (:minEnrollmentRatio IS NULL OR 
             (c.maxStudents > 0 AND (CAST(c.currentStudents AS FLOAT) / c.maxStudents) >= :minEnrollmentRatio))
        AND (:startDate IS NULL OR c.createdAt >= :startDate)
        AND (:endDate IS NULL OR c.createdAt <= :endDate)
        ORDER BY 
            CASE WHEN :sortBy = 'name' THEN c.name END ASC,
            CASE WHEN :sortBy = 'nameDesc' THEN c.name END DESC,
            CASE WHEN :sortBy = 'capacity' THEN c.maxStudents END ASC,
            CASE WHEN :sortBy = 'capacityDesc' THEN c.maxStudents END DESC,
            CASE WHEN :sortBy = 'students' THEN c.currentStudents END ASC,
            CASE WHEN :sortBy = 'studentsDesc' THEN c.currentStudents END DESC,
            CASE WHEN :sortBy = 'created' THEN c.createdAt END ASC,
            CASE WHEN :sortBy = 'createdDesc' THEN c.createdAt END DESC,
            c.createdAt DESC
        """)
    Page<Course> findCoursesByDynamicConditions(@Param("courseName") String courseName,
                                               @Param("instructorName") String instructorName,
                                               @Param("description") String description,
                                               @Param("minCapacity") Integer minCapacity,
                                               @Param("maxCapacity") Integer maxCapacity,
                                               @Param("minCurrentStudents") Integer minCurrentStudents,
                                               @Param("maxCurrentStudents") Integer maxCurrentStudents,
                                               @Param("isAvailable") Boolean isAvailable,
                                               @Param("minEnrollmentRatio") Double minEnrollmentRatio,
                                               @Param("startDate") java.time.LocalDateTime startDate,
                                               @Param("endDate") java.time.LocalDateTime endDate,
                                               @Param("sortBy") String sortBy,
                                               Pageable pageable);


    /**
     * 강의 통계 - 복잡한 집계 쿼리
     */
    @Query("""
        SELECT 
            c.name as courseName,
            i.fullName as instructorName,
            c.maxStudents as maxCapacity,
            c.currentStudents as currentStudents,
            CASE 
                WHEN c.maxStudents > 0 THEN (CAST(c.currentStudents AS FLOAT) / c.maxStudents) * 100
                ELSE 0 
            END as enrollmentRatio,
            c.createdAt as createdAt,
            CASE 
                WHEN c.currentStudents < c.maxStudents THEN 'AVAILABLE'
                ELSE 'FULL'
            END as status
        FROM Course c
        JOIN c.instructor i
        WHERE c.deleted = false
        AND (:courseName IS NULL OR c.name LIKE %:courseName%)
        AND (:instructorName IS NULL OR i.fullName LIKE %:instructorName%)
        AND (:minEnrollmentRatio IS NULL OR 
             (c.maxStudents > 0 AND (CAST(c.currentStudents AS FLOAT) / c.maxStudents) >= :minEnrollmentRatio))
        ORDER BY 
            CASE WHEN :sortBy = 'enrollmentRatio' THEN (CAST(c.currentStudents AS FLOAT) / c.maxStudents) END DESC,
            CASE WHEN :sortBy = 'capacity' THEN c.maxStudents END DESC,
            CASE WHEN :sortBy = 'students' THEN c.currentStudents END DESC,
            c.createdAt DESC
        """)
    java.util.List<Object[]> findCourseStatistics(@Param("courseName") String courseName,
                                                 @Param("instructorName") String instructorName,
                                                 @Param("minEnrollmentRatio") Double minEnrollmentRatio,
                                                 @Param("sortBy") String sortBy);

    /**
     * 강의 검색 - 텍스트 검색 (강의명, 설명, 강사명)
     */
    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor i
        WHERE c.deleted = false
        AND (:searchText IS NULL OR 
             c.name LIKE %:searchText% OR 
             c.description LIKE %:searchText% OR 
             i.fullName LIKE %:searchText%)
        ORDER BY 
            CASE WHEN :sortBy = 'relevance' THEN 
                CASE 
                    WHEN c.name LIKE %:searchText% THEN 1
                    WHEN c.description LIKE %:searchText% THEN 2
                    WHEN i.fullName LIKE %:searchText% THEN 3
                    ELSE 4
                END
            END ASC,
            c.createdAt DESC
        """)
    Page<Course> searchCoursesByText(@Param("searchText") String searchText,
                                    @Param("sortBy") String sortBy,
                                    Pageable pageable);
}
