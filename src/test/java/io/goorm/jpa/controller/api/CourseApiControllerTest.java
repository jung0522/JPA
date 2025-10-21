package io.goorm.jpa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.jpa.dto.course.CourseCreateRequest;
import io.goorm.jpa.dto.course.CourseResponse;
import io.goorm.jpa.dto.course.CourseUpdateRequest;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CourseApiController 테스트
 * @WebMvcTest를 사용한 Controller 계층 테스트
 */
@WebMvcTest(CourseApiController.class)
class CourseApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    private User instructor;
    private CourseResponse courseResponse;

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

        courseResponse = CourseResponse.builder()
                .courseNo(1L)
                .name("테스트 강의")
                .description("테스트 강의 설명")
                .maxStudents(30)
                .currentStudents(0)
                .instructorName("테스트 강사")
                .build();
    }

    @Test
    @DisplayName("강의 생성 API - 성공")
    void createCourse_성공() throws Exception {
        // given
        CourseCreateRequest request = CourseCreateRequest.builder()
                .name("새로운 강의")
                .description("새로운 강의 설명")
                .maxStudents(25)
                .instructorId(1L)
                .build();

        given(courseService.create(any(CourseCreateRequest.class))).willReturn(courseResponse);

        // when & then
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("테스트 강의"))
                .andExpect(jsonPath("$.data.instructorName").value("테스트 강사"));
    }

    @Test
    @DisplayName("강의 생성 API - 실패 (잘못된 요청)")
    void createCourse_실패_잘못된요청() throws Exception {
        // given
        CourseCreateRequest request = CourseCreateRequest.builder()
                .name("") // 빈 이름
                .description("새로운 강의 설명")
                .maxStudents(25)
                .instructorId(1L)
                .build();

        // when & then
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강의 조회 API - 성공")
    void getCourse_성공() throws Exception {
        // given
        given(courseService.getById(1L)).willReturn(courseResponse);

        // when & then
        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("테스트 강의"))
                .andExpect(jsonPath("$.data.instructorName").value("테스트 강사"));
    }

    @Test
    @DisplayName("강의 목록 조회 API - 성공")
    void getCourses_성공() throws Exception {
        // given
        List<CourseResponse> courses = List.of(courseResponse);
        Page<CourseResponse> coursePage = new PageImpl<>(courses);
        given(courseService.getAll(any())).willReturn(coursePage);

        // when & then
        mockMvc.perform(get("/api/courses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("테스트 강의"));
    }

    @Test
    @DisplayName("강의 수정 API - 성공")
    void updateCourse_성공() throws Exception {
        // given
        CourseUpdateRequest request = CourseUpdateRequest.builder()
                .name("수정된 강의")
                .description("수정된 강의 설명")
                .maxStudents(35)
                .build();

        CourseResponse updatedResponse = CourseResponse.builder()
                .courseNo(1L)
                .name("수정된 강의")
                .description("수정된 강의 설명")
                .maxStudents(35)
                .currentStudents(0)
                .instructorName("테스트 강사")
                .build();

        given(courseService.update(anyLong(), any(CourseUpdateRequest.class), any(User.class)))
                .willReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("수정된 강의"))
                .andExpect(jsonPath("$.data.maxStudents").value(35));
    }

    @Test
    @DisplayName("강의 삭제 API - 성공")
    void deleteCourse_성공() throws Exception {
        // given
        given(courseService.delete(anyLong(), any(User.class))).willReturn(null);

        // when & then
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("강의가 삭제되었습니다."));
    }

    @Test
    @DisplayName("수강 가능한 강의 목록 조회 API - 성공")
    void getAvailableCourses_성공() throws Exception {
        // given
        List<CourseResponse> availableCourses = List.of(courseResponse);
        given(courseService.getAvailableCourses()).willReturn(availableCourses);

        // when & then
        mockMvc.perform(get("/api/courses/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("테스트 강의"));
    }

    @Test
    @DisplayName("강의 검색 API - 성공")
    void searchCourses_성공() throws Exception {
        // given
        List<CourseResponse> searchResults = List.of(courseResponse);
        Page<CourseResponse> searchPage = new PageImpl<>(searchResults);
        given(courseService.searchCourses(any(), any())).willReturn(searchPage);

        // when & then
        mockMvc.perform(get("/api/courses/search")
                        .param("courseName", "Spring")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("테스트 강의"));
    }
}
