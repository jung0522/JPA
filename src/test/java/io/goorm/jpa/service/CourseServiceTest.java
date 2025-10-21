package io.goorm.jpa.service;

import io.goorm.jpa.dto.course.CourseCreateRequest;
import io.goorm.jpa.dto.course.CourseResponse;
import io.goorm.jpa.dto.course.CourseUpdateRequest;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * CourseService 테스트
 * @MockBean을 사용한 Service 계층 테스트
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseQueryRepository courseQueryRepository;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        instructor = User.builder()
                .userNo(1L)
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
    }

    @Test
    @DisplayName("강의 생성 - 성공")
    void createCourse_성공() {
        // given
        CourseCreateRequest request = CourseCreateRequest.builder()
                .name("새로운 강의")
                .description("새로운 강의 설명")
                .maxStudents(25)
                .instructorId(1L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(instructor));
        given(courseRepository.save(any(Course.class))).willReturn(course);

        // when
        CourseResponse response = courseService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("테스트 강의");
        assertThat(response.getInstructorName()).isEqualTo("테스트 강사");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("강의 생성 - 실패 (존재하지 않는 강사)")
    void createCourse_실패_존재하지않는강사() {
        // given
        CourseCreateRequest request = CourseCreateRequest.builder()
                .name("새로운 강의")
                .description("새로운 강의 설명")
                .maxStudents(25)
                .instructorId(999L)
                .build();

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("강의 조회 - 성공")
    void getCourse_성공() {
        // given
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        // when
        CourseResponse response = courseService.getById(1L);

        // then
        assertThat(response.getName()).isEqualTo("테스트 강의");
        assertThat(response.getInstructorName()).isEqualTo("테스트 강사");
    }

    @Test
    @DisplayName("강의 조회 - 실패 (존재하지 않는 강의)")
    void getCourse_실패_존재하지않는강의() {
        // given
        given(courseRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseService.getById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COURSE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("강의 목록 조회 - 성공")
    void getCourses_성공() {
        // given
        List<Course> courses = List.of(course);
        Page<Course> coursePage = new PageImpl<>(courses);
        given(courseRepository.findAll(any(PageRequest.class))).willReturn(coursePage);

        // when
        Page<CourseResponse> response = courseService.getAll(PageRequest.of(0, 10));

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("테스트 강의");
    }

    @Test
    @DisplayName("강의 수정 - 성공")
    void updateCourse_성공() {
        // given
        CourseUpdateRequest request = CourseUpdateRequest.builder()
                .name("수정된 강의")
                .description("수정된 강의 설명")
                .maxStudents(35)
                .build();

        given(courseRepository.findByIdAndInstructorForUpdate(1L, instructor)).willReturn(Optional.of(course));

        // when
        CourseResponse response = courseService.update(1L, request, instructor);

        // then
        assertThat(response.getName()).isEqualTo("수정된 강의");
        assertThat(response.getMaxStudents()).isEqualTo(35);
        verify(courseRepository).findByIdAndInstructorForUpdate(1L, instructor);
    }

    @Test
    @DisplayName("강의 수정 - 실패 (권한 없음)")
    void updateCourse_실패_권한없음() {
        // given
        CourseUpdateRequest request = CourseUpdateRequest.builder()
                .name("수정된 강의")
                .description("수정된 강의 설명")
                .maxStudents(35)
                .build();

        given(courseRepository.findByIdAndInstructorForUpdate(1L, instructor)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseService.update(1L, request, instructor))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COURSE_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("강의 삭제 - 성공")
    void deleteCourse_성공() {
        // given
        given(courseRepository.findByIdAndInstructorForDelete(1L, instructor)).willReturn(Optional.of(course));

        // when
        courseService.delete(1L, instructor);

        // then
        verify(courseRepository).findByIdAndInstructorForDelete(1L, instructor);
        verify(courseRepository).delete(course);
    }

    @Test
    @DisplayName("강의 삭제 - 실패 (권한 없음)")
    void deleteCourse_실패_권한없음() {
        // given
        given(courseRepository.findByIdAndInstructorForDelete(1L, instructor)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseService.delete(1L, instructor))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COURSE_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("수강 가능한 강의 목록 조회")
    void getAvailableCourses_성공() {
        // given
        List<Course> availableCourses = List.of(course);
        given(courseRepository.findAvailableCourses()).willReturn(availableCourses);

        // when
        List<CourseResponse> response = courseService.getAvailableCourses();

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("테스트 강의");
    }
}
