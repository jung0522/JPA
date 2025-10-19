package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 커리큘럼 엔티티
 * - ManyToOne 단방향 (Curriculum → Course)
 * - Step 1: JPQL 사용
 * - Step 2: 양방향 + 편의 메소드로 개선 예정
 */
@Entity
@Getter
@ToString(exclude = "course")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "curriculum")
public class Curriculum extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long curriculumNo;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String materials; // 준비물

    @Column
    private Integer duration; // 소요 시간 (분)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_no", nullable = false)
    private Course course;

    @Builder
    public Curriculum(Integer weekNumber, String title, String description, 
                     String materials, Integer duration, Course course) {
        this.weekNumber = weekNumber;
        this.title = title;
        this.description = description;
        this.materials = materials;
        this.duration = duration;
        this.course = course;
    }

    /**
     * 커리큘럼 수정
     */
    public void update(String title, String description, String materials, Integer duration) {
        this.title = title;
        this.description = description;
        this.materials = materials;
        this.duration = duration;
    }
}
