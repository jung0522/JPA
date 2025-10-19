package io.goorm.jpa.config;

import io.goorm.jpa.entity.Board;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.repository.BoardRepository;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Profile("jpa")
@RequiredArgsConstructor
public class JpaDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== JPA Data Initialization Started ===");

        // 중복 실행 방지
        if (userRepository.count() > 0) {
            log.info("=== 데이터가 이미 존재하여 초기화를 건너뜁니다. ===");
            return;
        }

        // Admin 사용자 생성
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .email("admin@goorm.io")
                .fullName("관리자")
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Admin user created: {}", admin.getUsername());

        // SecurityContext 설정 (Auditing을 위해)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                admin.getUserNo().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 강사 사용자 생성
        User instructor01 = User.builder()
                .username("instructor01")
                .password(passwordEncoder.encode("1234"))
                .email("instructor01@goorm.io")
                .fullName("김강사")
                .role(UserRole.INSTRUCTOR)
                .build();
        userRepository.save(instructor01);
        log.info("Instructor created: {}", instructor01.getUsername());

        // 학생 사용자 생성
        User student01 = User.builder()
                .username("student01")
                .password(passwordEncoder.encode("1234"))
                .email("student01@goorm.io")
                .fullName("홍길동")
                .role(UserRole.STUDENT)
                .build();
        userRepository.save(student01);
        log.info("Student created: {}", student01.getUsername());

        User student02 = User.builder()
                .username("student02")
                .password(passwordEncoder.encode("1234"))
                .email("student02@goorm.io")
                .fullName("김철수")
                .role(UserRole.STUDENT)
                .build();
        userRepository.save(student02);
        log.info("Student created: {}", student02.getUsername());

        // Board 테스트 데이터 생성
        Board board1 = Board.builder()
                .title("JPA 학습 가이드")
                .content("JPA를 효과적으로 학습하는 방법에 대해 공유합니다.\n\n1. 기본 개념 이해\n2. 실습 프로젝트 진행\n3. N+1 문제 해결\n4. 성능 최적화")
                .author(admin)
                .build();
        boardRepository.save(board1);

        Board board2 = Board.builder()
                .title("Spring Boot 3.5 새로운 기능")
                .content("Spring Boot 3.5 버전의 주요 변경사항을 정리했습니다.")
                .author(instructor01)
                .build();
        boardRepository.save(board2);

        Board board3 = Board.builder()
                .title("학사관리 시스템 사용법")
                .content("학사관리 시스템의 주요 기능 사용법입니다.\n\n- 강의 조회 및 수강신청\n- 내 수강 목록 확인\n- 프로필 관리")
                .author(admin)
                .build();
        boardRepository.save(board3);

        Board board4 = Board.builder()
                .title("Query Methods vs JPQL vs QueryDSL")
                .content("각 쿼리 작성 방법의 장단점을 비교합니다.")
                .author(instructor01)
                .build();
        boardRepository.save(board4);

        Board board5 = Board.builder()
                .title("수강신청 팁")
                .content("인기 강의는 빠르게 마감되니 서둘러 신청하세요!")
                .author(student01)
                .build();
        boardRepository.save(board5);

        log.info("Board test data created: {} boards", 5);

        // Course 테스트 데이터 생성
        Course course1 = Course.builder()
                .name("JPA 기초부터 실전까지")
                .description("JPA의 기본 개념부터 실무 활용까지 학습합니다.\n\n- Entity 설계\n- 연관관계 매핑\n- Query 작성\n- 성능 최적화")
                .instructor(instructor01)
                .maxStudents(30)
                .build();
        courseRepository.save(course1);

        Course course2 = Course.builder()
                .name("Spring Boot 완벽 가이드")
                .description("Spring Boot를 활용한 백엔드 개발 전문 과정입니다.")
                .instructor(instructor01)
                .maxStudents(25)
                .build();
        courseRepository.save(course2);

        Course course3 = Course.builder()
                .name("QueryDSL 마스터하기")
                .description("동적 쿼리 작성의 강자 QueryDSL을 마스터합니다.")
                .instructor(instructor01)
                .maxStudents(20)
                .build();
        courseRepository.save(course3);

        Course course4 = Course.builder()
                .name("데이터베이스 설계와 최적화")
                .description("효율적인 데이터베이스 설계 기법과 성능 최적화 방법을 학습합니다.")
                .instructor(instructor01)
                .maxStudents(35)
                .build();
        courseRepository.save(course4);

        Course course5 = Course.builder()
                .name("RESTful API 설계")
                .description("REST 원칙에 따른 API 설계와 구현 방법을 배웁니다.")
                .instructor(instructor01)
                .maxStudents(40)
                .build();
        courseRepository.save(course5);

        log.info("Course test data created: {} courses", 5);

        log.info("=== JPA Data Initialization Completed ===");
        log.info("Login credentials:");
        log.info("  Admin: admin / 1234");
        log.info("  Instructor: instructor01 / 1234");
        log.info("  Students: student01, student02 / 1234");
        log.info("Test data:");
        log.info("  Boards: 5 posts");
        log.info("  Courses: 5 courses");
    }
}
