package io.goorm.jpa.m2m.repository;

import io.goorm.jpa.m2m.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    @Query("""
        SELECT t FROM Tag t 
        WHERE t.name LIKE CONCAT('%', :keyword, '%')
        ORDER BY t.name ASC
        """)
    List<Tag> findByNameContaining(@Param("keyword") String keyword);
    
    @Query("""
        SELECT t FROM Tag t 
        WHERE t.name IN :names
        """)
    List<Tag> findByNameIn(@Param("names") List<String> names);
    
    // R-4: 태그별 게시글 수 집계
    @Query("""
        SELECT t.id, t.name, COUNT(pt) 
        FROM PostTag pt 
        JOIN pt.tag t 
        GROUP BY t.id, t.name 
        ORDER BY COUNT(pt) DESC
        """)
    List<Object[]> findTagCounts();
}
