package io.goorm.jpa.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentRequest;
import io.goorm.jpa.dto.enrollment.BatchEnrollmentResponse;
import io.goorm.jpa.dto.enrollment.EnrollmentCreateRequest;
import io.goorm.jpa.dto.enrollment.EnrollmentResponse;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.service.EnrollmentService;
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
 * EnrollmentApiController 테스트
 * 복잡한 API와 에러 처리 테스트
 */
@WebMvcTest(EnrollmentApiController.class)
class EnrollmentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    private User student;
    private EnrollmentResponse enrollmentResponse;

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

        enrollmentResponse = EnrollmentResponse.builder()
                .enrollmentNo(1L)
                .courseNo(1L)
                .courseName("테스트 강의")
                .studentName("테스트 학생")
                .status(EnrollmentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("수강신청 API - 성공")
    void enroll_성공() throws Exception {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(1L)
                .build();

        given(enrollmentService.enroll(any(EnrollmentCreateRequest.class), any(User.class)))
                .willReturn(enrollmentResponse);

        // when & then
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.courseName").value("테스트 강의"));
    }

    @Test
    @DisplayName("수강신청 API - 실패 (잘못된 요청)")
    void enroll_실패_잘못된요청() throws Exception {
        // given
        EnrollmentCreateRequest request = EnrollmentCreateRequest.builder()
                .courseNo(null) // null 값
                .build();

        // when & then
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수강신청 목록 조회 API - 성공")
    void getEnrollments_성공() throws Exception {
        // given
        List<EnrollmentResponse> enrollments = List.of(enrollmentResponse);
        Page<EnrollmentResponse> enrollmentPage = new PageImpl<>(enrollments);
        given(enrollmentService.getMyEnrollments(any())).willReturn(enrollmentPage);

        // when & then
        mockMvc.perform(get("/api/enrollments/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("수강신청 승인 API - 성공")
    void approveEnrollment_성공() throws Exception {
        // given
        EnrollmentResponse approvedResponse = EnrollmentResponse.builder()
                .enrollmentNo(1L)
                .courseNo(1L)
                .courseName("테스트 강의")
                .studentName("테스트 학생")
                .status(EnrollmentStatus.APPROVED)
                .build();

        given(enrollmentService.approve(1L)).willReturn(approvedResponse);

        // when & then
        mockMvc.perform(post("/api/enrollments/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    @DisplayName("수강신청 거절 API - 성공")
    void rejectEnrollment_성공() throws Exception {
        // given
        EnrollmentResponse rejectedResponse = EnrollmentResponse.builder()
                .enrollmentNo(1L)
                .courseNo(1L)
                .courseName("테스트 강의")
                .studentName("테스트 학생")
                .status(EnrollmentStatus.REJECTED)
                .build();

        given(enrollmentService.reject(1L, "정원 초과")).willReturn(rejectedResponse);

        // when & then
        mockMvc.perform(post("/api/enrollments/1/reject")
                        .param("reason", "정원 초과"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));
    }

    @Test
    @DisplayName("수강신청 취소 API - 성공")
    void cancelEnrollment_성공() throws Exception {
        // given
        EnrollmentResponse cancelledResponse = EnrollmentResponse.builder()
                .enrollmentNo(1L)
                .courseNo(1L)
                .courseName("테스트 강의")
                .studentName("테스트 학생")
                .status(EnrollmentStatus.CANCELLED)
                .build();

        given(enrollmentService.cancel(1L, student)).willReturn(cancelledResponse);

        // when & then
        mockMvc.perform(delete("/api/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("배치 승인 처리 API - 성공")
    void batchApprove_성공() throws Exception {
        // given
        BatchEnrollmentRequest request = BatchEnrollmentRequest.builder()
                .enrollmentIds(List.of(1L, 2L, 3L))
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .reason("일괄 승인 처리")
                .build();

        BatchEnrollmentResponse response = BatchEnrollmentResponse.builder()
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .totalCount(3)
                .successCount(3)
                .failureCount(0)
                .build();

        given(enrollmentService.batchProcessEnrollments(any(BatchEnrollmentRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/enrollments/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(3))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failureCount").value(0));
    }

    @Test
    @DisplayName("배치 처리 API - 실패 (잘못된 요청)")
    void batchProcess_실패_잘못된요청() throws Exception {
        // given
        BatchEnrollmentRequest request = BatchEnrollmentRequest.builder()
                .enrollmentIds(List.of()) // 빈 목록
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .reason("일괄 승인 처리")
                .build();

        // when & then
        mockMvc.perform(post("/api/enrollments/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("강의별 배치 처리 API - 성공")
    void batchProcessByCourse_성공() throws Exception {
        // given
        BatchEnrollmentResponse response = BatchEnrollmentResponse.builder()
                .action(BatchEnrollmentRequest.BatchAction.APPROVE)
                .totalCount(2)
                .successCount(2)
                .failureCount(0)
                .build();

        given(enrollmentService.batchProcessEnrollmentsByCourse(1L, BatchEnrollmentRequest.BatchAction.APPROVE, "강의별 일괄 승인"))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/enrollments/batch/course/1")
                        .param("action", "APPROVE")
                        .param("reason", "강의별 일괄 승인"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.successCount").value(2));
    }
}
