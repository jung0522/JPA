package io.goorm.jpa.config;

import io.goorm.jpa.entity.Board;
import io.goorm.jpa.entity.Course;
import io.goorm.jpa.entity.Curriculum;
import io.goorm.jpa.entity.Enrollment;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.EnrollmentStatus;
import io.goorm.jpa.enums.UserRole;
import io.goorm.jpa.repository.BoardRepository;
import io.goorm.jpa.repository.CourseRepository;
import io.goorm.jpa.repository.EnrollmentRepository;
import io.goorm.jpa.repository.UserRepository;
import io.goorm.jpa.repository.CurriculumRepository;
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
    private final EnrollmentRepository enrollmentRepository;
    private final CurriculumRepository curriculumRepository;
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

        User instructor02 = User.builder()
                .username("instructor02")
                .password(passwordEncoder.encode("1234"))
                .email("instructor02@goorm.io")
                .fullName("이강사")
                .role(UserRole.INSTRUCTOR)
                .build();
        userRepository.save(instructor02);
        log.info("Instructor created: {}", instructor02.getUsername());

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

        Board board6 = Board.builder()
                .title("JPA N+1 문제 해결 방법")
                .content("N+1 문제는 JPA를 사용할 때 가장 흔한 성능 이슈입니다.\n\n해결 방법:\n1. Fetch Join 사용\n2. @EntityGraph 활용\n3. Batch Size 설정")
                .author(instructor01)
                .build();
        boardRepository.save(board6);

        Board board7 = Board.builder()
                .title("Spring Data JPA Best Practices")
                .content("Spring Data JPA를 효과적으로 사용하기 위한 베스트 프랙티스를 공유합니다.")
                .author(admin)
                .build();
        boardRepository.save(board7);

        Board board8 = Board.builder()
                .title("QueryDSL 동적 쿼리 작성법")
                .content("QueryDSL을 활용한 복잡한 동적 쿼리 작성 예제입니다.")
                .author(instructor02)
                .build();
        boardRepository.save(board8);

        Board board9 = Board.builder()
                .title("Soft Delete vs Hard Delete")
                .content("데이터 삭제 전략에 대한 고민과 각 방식의 장단점을 정리했습니다.")
                .author(admin)
                .build();
        boardRepository.save(board9);

        Board board10 = Board.builder()
                .title("Optimistic Lock 활용 사례")
                .content("낙관적 잠금을 활용한 동시성 제어 방법을 설명합니다.")
                .author(instructor02)
                .build();
        boardRepository.save(board10);

        Board board11 = Board.builder()
                .title("JPA Auditing 설정하기")
                .content("@CreatedDate, @LastModifiedDate를 활용한 감사(Audit) 기능 구현 방법입니다.")
                .author(admin)
                .build();
        boardRepository.save(board11);

        Board board12 = Board.builder()
                .title("Entity 설계 시 주의사항")
                .content("Entity를 설계할 때 반드시 고려해야 할 사항들을 정리했습니다.\n\n- @Id 전략\n- 연관관계 주인\n- 즉시/지연 로딩")
                .author(instructor02)
                .build();
        boardRepository.save(board12);

        Board board13 = Board.builder()
                .title("DTO vs Entity")
                .content("DTO와 Entity의 차이점과 언제 어떤 것을 사용해야 하는지 설명합니다.")
                .author(admin)
                .build();
        boardRepository.save(board13);

        Board board14 = Board.builder()
                .title("Pagination 구현 방법")
                .content("Spring Data JPA에서 페이징을 구현하는 다양한 방법을 소개합니다.")
                .author(instructor02)
                .build();
        boardRepository.save(board14);

        Board board15 = Board.builder()
                .title("데이터베이스 인덱스 전략")
                .content("JPA에서 @Index를 활용한 인덱스 설계 방법입니다.")
                .author(admin)
                .build();
        boardRepository.save(board15);

        Board board16 = Board.builder()
                .title("강의 후기 - JPA 기초 과정")
                .content("JPA 기초 과정을 수강하고 정말 많은 도움이 되었습니다!")
                .author(student02)
                .build();
        boardRepository.save(board16);

        log.info("Board test data created: {} boards", 16);

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
                .instructor(instructor02)
                .maxStudents(20)
                .build();
        courseRepository.save(course3);

        Course course4 = Course.builder()
                .name("데이터베이스 설계와 최적화")
                .description("효율적인 데이터베이스 설계 기법과 성능 최적화 방법을 학습합니다.")
                .instructor(instructor02)
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

        // Enrollment 테스트 데이터 생성
        // student01이 course1, course2에 수강신청
        Enrollment enrollment1 = Enrollment.builder()
                .student(student01)
                .course(course1)
                .build();
        enrollment1.approve(); // 승인 상태로 설정
        enrollmentRepository.save(enrollment1);

        Enrollment enrollment2 = Enrollment.builder()
                .student(student01)
                .course(course2)
                .build();
        enrollment2.approve(); // 승인 상태로 설정
        enrollmentRepository.save(enrollment2);

        // student02가 course1, course3에 수강신청
        Enrollment enrollment3 = Enrollment.builder()
                .student(student02)
                .course(course1)
                .build();
        enrollment3.approve(); // 승인 상태로 설정
        enrollmentRepository.save(enrollment3);

        Enrollment enrollment4 = Enrollment.builder()
                .student(student02)
                .course(course3)
                .build();
        enrollment4.approve(); // 승인 상태로 설정
        enrollmentRepository.save(enrollment4);

        // student01이 course4에 대기 중인 수강신청
        Enrollment enrollment5 = Enrollment.builder()
                .student(student01)
                .course(course4)
                .build();
        // PENDING 상태로 유지 (승인하지 않음)
        enrollmentRepository.save(enrollment5);

        // student02가 course5에 거절된 수강신청
        Enrollment enrollment6 = Enrollment.builder()
                .student(student02)
                .course(course5)
                .build();
        enrollment6.reject(); // 거절 상태로 설정
        enrollmentRepository.save(enrollment6);

        log.info("Enrollment test data created: {} enrollments", 6);

        // Curriculum 테스트 데이터 생성
        // course1의 커리큘럼
        Curriculum curriculum1 = Curriculum.builder()
                .weekNumber(1)
                .title("JPA 소개")
                .description("JPA의 기본 개념과 설정 방법을 학습합니다.")
                .materials("노트북, JPA 공식 문서")
                .duration(90)
                .course(course1)
                .build();
        curriculumRepository.save(curriculum1);

        Curriculum curriculum2 = Curriculum.builder()
                .weekNumber(2)
                .title("엔티티 매핑")
                .description("엔티티와 테이블 매핑 방법을 학습합니다.")
                .materials("노트북, 예제 코드")
                .duration(120)
                .course(course1)
                .build();
        curriculumRepository.save(curriculum2);

        Curriculum curriculum3 = Curriculum.builder()
                .weekNumber(3)
                .title("관계 매핑")
                .description("OneToOne, OneToMany, ManyToOne 관계를 학습합니다.")
                .materials("노트북, 실습 프로젝트")
                .duration(150)
                .course(course1)
                .build();
        curriculumRepository.save(curriculum3);

        // course2의 커리큘럼
        Curriculum curriculum4 = Curriculum.builder()
                .weekNumber(1)
                .title("Spring Security 기초")
                .description("Spring Security의 기본 개념을 학습합니다.")
                .materials("노트북, Spring Security 문서")
                .duration(90)
                .course(course2)
                .build();
        curriculumRepository.save(curriculum4);

        Curriculum curriculum5 = Curriculum.builder()
                .weekNumber(2)
                .title("인증과 인가")
                .description("사용자 인증과 권한 관리 방법을 학습합니다.")
                .materials("노트북, 예제 프로젝트")
                .duration(120)
                .course(course2)
                .build();
        curriculumRepository.save(curriculum5);

        log.info("Curriculum test data created: {} curriculums", 5);

        log.info("=== JPA Data Initialization Completed ===");
        log.info("Login credentials:");
        log.info("  Admin: admin / 1234");
        log.info("  Instructors: instructor01, instructor02 / 1234");
        log.info("  Students: student01, student02 / 1234");
        log.info("Test data:");
        log.info("  Boards: 16 posts");
        log.info("  Courses: 5 courses");
        log.info("  Enrollments: 6 enrollments");
        log.info("  Curriculums: 5 curriculums");
    }
}
