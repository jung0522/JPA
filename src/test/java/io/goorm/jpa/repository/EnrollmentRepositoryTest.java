package io.goorm.jpa.repository;

import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EnrollmentRepository 테스트
 * 복잡한 쿼리와 락킹 기능 테스트
 */
@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User student;
    private User instructor;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        // 테스트용 학생 생성
        student = User.builder()
                .username("test-student")
                .password("password")
                .email("student@test.com")
                .fullName("테스트 학생")
                .role(UserRole.STUDENT)
                .build();
        entityManager.persistAndFlush(student);

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

        // 테스트용 수강신청 생성
        enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();
        entityManager.persistAndFlush(enrollment);
    }

    @Test
    @DisplayName("수강신청 ID로 조회 - 성공")
    void findById_성공() {
        // when
        Optional<Enrollment> foundEnrollment = enrollmentRepository.findById(enrollment.getEnrollmentNo());

        // then
        assertThat(foundEnrollment).isPresent();
        assertThat(foundEnrollment.get().getStudent().getFullName()).isEqualTo("테스트 학생");
        assertThat(foundEnrollment.get().getCourse().getName()).isEqualTo("테스트 강의");
        assertThat(foundEnrollment.get().getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    @DisplayName("학생별 수강신청 목록 조회")
    void findByStudent() {
        // given - 추가 수강신청 생성
        Course course2 = Course.builder()
                .name("테스트 강의 2")
                .description("테스트 강의 설명 2")
                .maxStudents(20)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(course2);

        Enrollment enrollment2 = Enrollment.builder()
                .student(student)
                .course(course2)
                .status(EnrollmentStatus.APPROVED)
                .build();
        entityManager.persistAndFlush(enrollment2);

        // when
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        // then
        assertThat(enrollments).hasSize(2);
        assertThat(enrollments).extracting("course.name")
                .containsExactlyInAnyOrder("테스트 강의", "테스트 강의 2");
    }

    @Test
    @DisplayName("강의별 수강신청 목록 조회")
    void findByCourse() {
        // given - 다른 학생의 수강신청 생성
        User student2 = User.builder()
                .username("test-student2")
                .password("password")
                .email("student2@test.com")
                .fullName("테스트 학생 2")
                .role(UserRole.STUDENT)
                .build();
        entityManager.persistAndFlush(student2);

        Enrollment enrollment2 = Enrollment.builder()
                .student(student2)
                .course(course)
                .status(EnrollmentStatus.APPROVED)
                .build();
        entityManager.persistAndFlush(enrollment2);

        // when
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);

        // then
        assertThat(enrollments).hasSize(2);
        assertThat(enrollments).extracting("student.fullName")
                .containsExactlyInAnyOrder("테스트 학생", "테스트 학생 2");
    }

    @Test
    @DisplayName("상태별 수강신청 조회")
    void findByStatus() {
        // given - 승인된 수강신청 생성
        User student2 = User.builder()
                .username("test-student2")
                .password("password")
                .email("student2@test.com")
                .fullName("테스트 학생 2")
                .role(UserRole.STUDENT)
                .build();
        entityManager.persistAndFlush(student2);

        Enrollment approvedEnrollment = Enrollment.builder()
                .student(student2)
                .course(course)
                .status(EnrollmentStatus.APPROVED)
                .build();
        entityManager.persistAndFlush(approvedEnrollment);

        // when
        List<Enrollment> pendingEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.PENDING);
        List<Enrollment> approvedEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.APPROVED);

        // then
        assertThat(pendingEnrollments).hasSize(1);
        assertThat(pendingEnrollments.get(0).getStudent().getFullName()).isEqualTo("테스트 학생");

        assertThat(approvedEnrollments).hasSize(1);
        assertThat(approvedEnrollments.get(0).getStudent().getFullName()).isEqualTo("테스트 학생 2");
    }

    @Test
    @DisplayName("학생-강의별 중복 수강신청 확인")
    void findByStudentAndCourse() {
        // when
        Optional<Enrollment> foundEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);

        // then
        assertThat(foundEnrollment).isPresent();
        assertThat(foundEnrollment.get().getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    @DisplayName("존재하지 않는 학생-강의 조합")
    void findByStudentAndCourse_존재하지않음() {
        // given - 다른 강의 생성
        Course otherCourse = Course.builder()
                .name("다른 강의")
                .description("다른 강의 설명")
                .maxStudents(20)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        entityManager.persistAndFlush(otherCourse);

        // when
        Optional<Enrollment> foundEnrollment = enrollmentRepository.findByStudentAndCourse(student, otherCourse);

        // then
        assertThat(foundEnrollment).isEmpty();
    }

    @Test
    @DisplayName("강의별 대기 중인 수강신청 조회")
    void findPendingByCourse() {
        // given - 승인된 수강신청 추가
        User student2 = User.builder()
                .username("test-student2")
                .password("password")
                .email("student2@test.com")
                .fullName("테스트 학생 2")
                .role(UserRole.STUDENT)
                .build();
        entityManager.persistAndFlush(student2);

        Enrollment approvedEnrollment = Enrollment.builder()
                .student(student2)
                .course(course)
                .status(EnrollmentStatus.APPROVED)
                .build();
        entityManager.persistAndFlush(approvedEnrollment);

        // when
        List<Enrollment> pendingEnrollments = enrollmentRepository.findPendingByCourse(course);

        // then
        assertThat(pendingEnrollments).hasSize(1);
        assertThat(pendingEnrollments.get(0).getStudent().getFullName()).isEqualTo("테스트 학생");
        assertThat(pendingEnrollments.get(0).getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    @DisplayName("수강신청 삭제")
    void deleteEnrollment() {
        // given
        Long enrollmentId = enrollment.getEnrollmentNo();

        // when
        enrollmentRepository.delete(enrollment);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Enrollment> deletedEnrollment = enrollmentRepository.findById(enrollmentId);
        assertThat(deletedEnrollment).isEmpty();
    }
}
