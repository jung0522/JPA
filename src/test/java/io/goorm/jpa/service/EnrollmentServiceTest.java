package io.goorm.jpa.service;

import io.goorm.jpa.dto.enrollment.BatchEnrollmentRequest;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentResponse;
import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import io.goorm.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * EnrollmentService 테스트
 * 비즈니스 로직과 예외 처리 테스트
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User student;
    private User instructor;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .userNo(1L)
                .username("test-student")
                .password("password")
                .email("student@test.com")
                .fullName("테스트 학생")
                .role(UserRole.STUDENT)
                .build();

        instructor = User.builder()
                .userNo(2L)
                .username("test-instructor")
                .password("password")
                .email("instructor@test.com")
                .fullName("테스트 강사")
                .role(UserRole.INSTRUCTOR)
                .build();

        course = Course.builder()
                .courseNo(1L)
                .name("테스트 강의")
                .description("테스트 강의 설명")
                .maxStudents(30)
                .currentStudents(0)
                .instructor(instructor)
                .build();

        enrollment = Enrollment.builder()
                .enrollmentNo(1L)
                .student(student)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("수강신청 - 성공")
    void enroll_성공() {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(1L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(student));
        given(courseRepository.findAvailableByIdForEnrollment(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.findByStudentAndCourseForUpdate(student, course)).willReturn(Optional.empty());
        given(enrollmentRepository.save(any(Enrollment.class))).willReturn(enrollment);

        // when
        EnrollmentResponse response = enrollmentService.enroll(request, student);

        // then
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("수강신청 - 실패 (정원 초과)")
    void enroll_실패_정원초과() {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(1L)
                .build();

        Course fullCourse = Course.builder()
                .courseNo(1L)
                .name("정원 가득찬 강의")
                .maxStudents(10)
                .currentStudents(10) // 정원 가득참
                .instructor(instructor)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(student));
        given(courseRepository.findAvailableByIdForEnrollment(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentService.enroll(request, student))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COURSE_FULL.getMessage());
    }

    @Test
    @DisplayName("수강신청 - 실패 (중복 신청)")
    void enroll_실패_중복신청() {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(1L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(student));
        given(courseRepository.findAvailableByIdForEnrollment(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.findByStudentAndCourseForUpdate(student, course)).willReturn(Optional.of(enrollment));

        // when & then
        assertThatThrownBy(() -> enrollmentService.enroll(request, student))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ENROLLMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("수강신청 승인 - 성공")
    void approveEnrollment_성공() {
        // given
        given(enrollmentRepository.findByIdForUpdate(1L)).willReturn(Optional.of(enrollment));

        // when
        EnrollmentResponse response = enrollmentService.approve(1L);

        // then
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
        verify(enrollmentRepository).findByIdForUpdate(1L);
    }

    @Test
    @DisplayName("수강신청 승인 - 실패 (존재하지 않는 수강신청)")
    void approveEnrollment_실패_존재하지않음() {
        // given
        given(enrollmentRepository.findByIdForUpdate(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> enrollmentService.approve(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ENROLLMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("수강신청 거절 - 성공")
    void rejectEnrollment_성공() {
        // given
        given(enrollmentRepository.findByIdForUpdate(1L)).willReturn(Optional.of(enrollment));

        // when
        EnrollmentResponse response = enrollmentService.reject(1L, "정원 초과");

        // then
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.REJECTED);
        verify(enrollmentRepository).findByIdForUpdate(1L);
    }

    @Test
    @DisplayName("수강신청 취소 - 성공")
    void cancelEnrollment_성공() {
        // given
        given(enrollmentRepository.findByIdForUpdate(1L)).willReturn(Optional.of(enrollment));

        // when
        EnrollmentResponse response = enrollmentService.cancel(1L, student);

        // then
        assertThat(response.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        verify(enrollmentRepository).findByIdForUpdate(1L);
    }

    @Test
    @DisplayName("배치 승인 처리 - 성공")
    void batchApprove_성공() {
        // given
        BatchEnrollmentRequest request = BatchEnrollmentRequest.builder()
                .enrollmentIds(List.of(1L, 2L))
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .reason("일괄 승인")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .enrollmentNo(2L)
                .student(student)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

        given(enrollmentRepository.findByIdsForBatchUpdate(List.of(1L, 2L))).willReturn(List.of(enrollment, enrollment2));

        // when
        BatchEnrollmentResponse response = enrollmentService.batchProcessEnrollments(request);

        // then
        assertThat(response.getSuccessCount()).isEqualTo(2);
        assertThat(response.getFailureCount()).isEqualTo(0);
        assertThat(response.getAction()).isEqualTo(BatchEnrollmentRequest.BatchAction.APPROVE);
    }

    @Test
    @DisplayName("배치 처리 - 실패 (처리할 수강신청 없음)")
    void batchProcess_실패_처리할수강신청없음() {
        // given
        BatchEnrollmentRequest request = BatchEnrollmentRequest.builder()
                .enrollmentIds(List.of(999L))
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .reason("일괄 승인")
                .build();

        given(enrollmentRepository.findByIdsForBatchUpdate(List.of(999L))).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> enrollmentService.batchProcessEnrollments(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ENROLLMENT_BATCH_NO_ITEMS.getMessage());
    }
}
