package io.goorm.jpa.entity;

import io.goorm.jpa.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 강의 엔티티
 * - ManyToOne 단방향 (Course → User)
 * - JPQL 사용
 * - Pessimistic Lock (Step 2)
 */
@Entity
@Getter
@ToString(exclude = "instructor")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long courseNo;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer maxStudents;

    @Column(nullable = false)
    private Integer currentStudents = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_no", nullable = false)
    private User instructor;

    @Builder
    public Course(String name, String description, Integer maxStudents, User instructor) {
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
        this.instructor = instructor;
        this.currentStudents = 0;
    }

    /**
     * 강의 수정
     */
    public void update(String name, String description, Integer maxStudents) {
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
    }

    /**
     * 수강생 수 증가
     */
    public void increaseCurrentStudents() {
        this.currentStudents++;
    }

    /**
     * 수강생 수 감소
     */
    public void decreaseCurrentStudents() {
        if (this.currentStudents > 0) {
            this.currentStudents--;
        }
    }

    /**
     * 수강 가능 여부
     */
    public boolean isAvailable() {
        return this.currentStudents < this.maxStudents;
    }

    /**
     * 강사 확인
     */
    public boolean isInstructor(User user) {
        return this.instructor.equals(user);
    }
}
