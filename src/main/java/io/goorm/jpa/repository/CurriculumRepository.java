package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Curriculum Repository
 * - JPQL 사용
 * - ManyToOne 단방향
 */
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    /**
     * 강의별 커리큘럼 조회 (주차순 정렬)
     */
    @Query("SELECT c FROM Curriculum c WHERE c.course.courseNo = :courseNo ORDER BY c.weekNumber ASC")
    List<Curriculum> findByCourseNoOrderByWeekNumber(@Param("courseNo") Long courseNo);

    /**
     * 강의별 커리큘럼 개수
     */
    @Query("SELECT COUNT(c) FROM Curriculum c WHERE c.course.courseNo = :courseNo")
    Long countByCourseNo(@Param("courseNo") Long courseNo);

    /**
     * 특정 주차 커리큘럼 조회
     */
    @Query("SELECT c FROM Curriculum c WHERE c.course.courseNo = :courseNo AND c.weekNumber = :weekNumber")
    Curriculum findByCourseNoAndWeekNumber(@Param("courseNo") Long courseNo, @Param("weekNumber") Integer weekNumber);
}
