package io.goorm.jpa.integration;

import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import io.goorm.jpa.repository.UserRepository;
import io.goorm.jpa.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Enrollment 통합 테스트
 * @SpringBootTest를 사용한 전체 플로우 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnrollmentIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User student;
    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        // 테스트용 학생 생성
        student = User.builder()
                .username("integration-student")
                .password("password")
                .email("integration-student@test.com")
                .fullName("통합테스트 학생")
                .role(UserRole.STUDENT)
                .build();
        userRepository.save(student);

        // 테스트용 강사 생성
        instructor = User.builder()
                .username("integration-instructor")
                .password("password")
                .email("integration-instructor@test.com")
                .fullName("통합테스트 강사")
                .role(UserRole.INSTRUCTOR)
                .build();
        userRepository.save(instructor);

        // 테스트용 강의 생성
        course = Course.builder()
                .name("통합테스트 강의")
                .description("통합테스트 강의 설명")
                .maxStudents(30)
                .currentStudents(0)
                .instructor(instructor)
                .build();
        courseRepository.save(course);
    }

    @Test
    @DisplayName("수강신청 전체 플로우 - 성공")
    void enrollmentFullFlow_성공() {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();

        // when - 수강신청
        EnrollmentResponse enrollmentResponse = enrollmentService.enroll(request, student);

        // then - 수강신청 확인
        assertThat(enrollmentResponse.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(enrollmentResponse.getCourseName()).isEqualTo("통합테스트 강의");
        assertThat(enrollmentResponse.getStudentName()).isEqualTo("통합테스트 학생");

        // when - 수강신청 승인
        EnrollmentResponse approvedResponse = enrollmentService.approve(enrollmentResponse.getEnrollmentNo());

        // then - 승인 확인
        assertThat(approvedResponse.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);

        // when - 강의 정보 확인
        Course updatedCourse = courseRepository.findById(course.getCourseNo()).orElseThrow();
        List<EnrollmentResponse> studentEnrollments = enrollmentService.getMyEnrollments(student);

        // then - 강의 정원 증가 확인
        assertThat(updatedCourse.getCurrentStudents()).isEqualTo(1);
        assertThat(studentEnrollments).hasSize(1);
        assertThat(studentEnrollments.get(0).getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
    }

    @Test
    @DisplayName("수강신청 취소 플로우 - 성공")
    void enrollmentCancelFlow_성공() {
        // given - 수강신청 생성
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();
        EnrollmentResponse enrollmentResponse = enrollmentService.enroll(request, student);

        // when - 수강신청 취소
        EnrollmentResponse cancelledResponse = enrollmentService.cancel(enrollmentResponse.getEnrollmentNo(), student);

        // then - 취소 확인
        assertThat(cancelledResponse.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);

        // when - 강의 정보 확인
        Course updatedCourse = courseRepository.findById(course.getCourseNo()).orElseThrow();

        // then - 강의 정원 변화 없음 확인
        assertThat(updatedCourse.getCurrentStudents()).isEqualTo(0);
    }

    @Test
    @DisplayName("수강신청 거절 플로우 - 성공")
    void enrollmentRejectFlow_성공() {
        // given - 수강신청 생성
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();
        EnrollmentResponse enrollmentResponse = enrollmentService.enroll(request, student);

        // when - 수강신청 거절
        EnrollmentResponse rejectedResponse = enrollmentService.reject(enrollmentResponse.getEnrollmentNo(), "정원 초과");

        // then - 거절 확인
        assertThat(rejectedResponse.getStatus()).isEqualTo(EnrollmentStatus.REJECTED);

        // when - 강의 정보 확인
        Course updatedCourse = courseRepository.findById(course.getCourseNo()).orElseThrow();

        // then - 강의 정원 변화 없음 확인
        assertThat(updatedCourse.getCurrentStudents()).isEqualTo(0);
    }

    @Test
    @DisplayName("중복 수강신청 방지 - 실패")
    void duplicateEnrollment_실패() {
        // given - 첫 번째 수강신청
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();
        enrollmentService.enroll(request, student);

        // when & then - 두 번째 수강신청 시도 (중복)
        try {
            enrollmentService.enroll(request, student);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("이미 수강신청한 강의입니다");
        }

        // then - 수강신청은 1개만 존재
        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(student);
        assertThat(enrollments).hasSize(1);
    }

    @Test
    @DisplayName("정원 초과 수강신청 - 실패")
    void overCapacityEnrollment_실패() {
        // given - 정원이 가득 찬 강의 생성
        Course fullCourse = Course.builder()
                .name("정원 가득찬 강의")
                .description("정원이 가득 찬 강의")
                .maxStudents(1)
                .currentStudents(1) // 정원 가득참
                .instructor(instructor)
                .build();
        courseRepository.save(fullCourse);

        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(fullCourse.getCourseNo())
                .build();

        // when & then - 정원 초과 수강신청 시도
        try {
            enrollmentService.enroll(request, student);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("강의 정원이 가득 찼습니다");
        }
    }

    @Test
    @DisplayName("강의별 수강신청 목록 조회")
    void getEnrollmentsByCourse_성공() {
        // given - 여러 학생의 수강신청 생성
        User student2 = User.builder()
                .username("integration-student2")
                .password("password")
                .email("integration-student2@test.com")
                .fullName("통합테스트 학생 2")
                .role(UserRole.STUDENT)
                .build();
        userRepository.save(student2);

        EnrollmentCreateRequest request1 = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();
        enrollmentService.enroll(request1, student);

        EnrollmentCreateRequest request2 = EnrollmentCreateRequest.builder()
                .courseNo(course.getCourseNo())
                .build();
        enrollmentService.enroll(request2, student2);

        // when - 강의별 수강신청 목록 조회
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourse(course.getCourseNo());

        // then - 수강신청 2개 확인
        assertThat(enrollments).hasSize(2);
        assertThat(enrollments).extracting("studentName")
                .containsExactlyInAnyOrder("통합테스트 학생", "통합테스트 학생 2");
    }
}
