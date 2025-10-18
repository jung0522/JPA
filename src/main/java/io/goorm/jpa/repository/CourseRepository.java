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
}
