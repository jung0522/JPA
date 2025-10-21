package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CourseRepository 테스트
 * @DataJpaTest를 사용한 Repository 계층 테스트
 */
@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        // 테스트용 강사 생성
        instructor = User.builder()
                .username("test-instructor")
                .password("password")
                .email("instructor@test.com")
                .fullName("테스트 강사")
                .role(UserRole.INSTRUCTOR)
                .build();
        entityManager.persistAndFlush(instructor);

        // 테스트용 강의 생성
        course = Course.builder()
                .name("테스트 강의")
                .description("테스트 강의 설명")
                .maxStudents(30)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(course);
    }

    @Test
    @DisplayName("강의 ID로 조회 - 성공")
    void findById_성공() {
        // when
        Optional<Course> foundCourse = courseRepository.findById(course.getCourseNo());

        // then
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getName()).isEqualTo("테스트 강의");
        assertThat(foundCourse.get().getInstructor().getFullName()).isEqualTo("테스트 강사");
    }

    @Test
    @DisplayName("강의 ID로 조회 - 실패 (존재하지 않는 ID)")
    void findById_실패() {
        // when
        Optional<Course> foundCourse = courseRepository.findById(999L);

        // then
        assertThat(foundCourse).isEmpty();
    }

    @Test
    @DisplayName("강사별 강의 목록 조회")
    void findByInstructor() {
        // given - 추가 강의 생성
        Course course2 = Course.builder()
                .name("테스트 강의 2")
                .description("테스트 강의 설명 2")
                .maxStudents(20)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(course2);

        // when
        List<Course> courses = courseRepository.findByInstructor(instructor);

        // then
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting("name")
                .containsExactlyInAnyOrder("테스트 강의", "테스트 강의 2");
    }

    @Test
    @DisplayName("수강 가능한 강의 목록 조회")
    void findAvailableCourses() {
        // given - 정원이 가득 찬 강의 생성
        Course fullCourse = Course.builder()
                .name("정원 가득찬 강의")
                .description("정원이 가득 찬 강의")
                .maxStudents(10)
                .currentStudents(10) // 정원 가득참
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(fullCourse);

        // when
        List<Course> availableCourses = courseRepository.findAvailableCourses();

        // then
        assertThat(availableCourses).hasSize(1);
        assertThat(availableCourses.get(0).getName()).isEqualTo("테스트 강의");
    }

    @Test
    @DisplayName("강의 페이징 조회")
    void findAllWithPaging() {
        // given - 추가 강의들 생성
        for (int i = 1; i <= 5; i++) {
            Course additionalCourse = Course.builder()
                    .name("추가 강의 " + i)
                    .description("추가 강의 설명 " + i)
                    .maxStudents(30)
                    .currentStudents(0)
                    .instructor(instructor)
                    .build();
            entityManager.persistAndFlush(additionalCourse);
        }

        // when
        Page<Course> coursePage = courseRepository.findAll(PageRequest.of(0, 3));

        // then
        assertThat(coursePage.getContent()).hasSize(3);
        assertThat(coursePage.getTotalElements()).isEqualTo(6); // 기존 1개 + 추가 5개
        assertThat(coursePage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("강의명으로 검색")
    void findByNameContaining() {
        // given - 검색할 강의 생성
        Course searchCourse = Course.builder()
                .name("Spring Boot 강의")
                .description("Spring Boot 설명")
                .maxStudents(25)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(searchCourse);

        // when
        List<Course> foundCourses = courseRepository.findByNameContaining("Spring");

        // then
        assertThat(foundCourses).hasSize(1);
        assertThat(foundCourses.get(0).getName()).isEqualTo("Spring Boot 강의");
    }

    @Test
    @DisplayName("강의 삭제 (Soft Delete)")
    void deleteCourse() {
        // given
        Long courseId = course.getCourseNo();

        // when
        courseRepository.delete(course);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Course> deletedCourse = courseRepository.findById(courseId);
        assertThat(deletedCourse).isEmpty();
    }
}
