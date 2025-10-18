package io.goorm.jpa.config;

import io.goorm.jpa.entity.User;
import io.goorm.jpa.enums.UserRole;
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

        // 일반 사용자 생성
        User student01 = User.builder()
                .username("student01")
                .password(passwordEncoder.encode("1234"))
                .email("student01@goorm.io")
                .fullName("학생01")
                .role(UserRole.USER)
                .build();
        userRepository.save(student01);
        log.info("User created: {}", student01.getUsername());

        User student02 = User.builder()
                .username("student02")
                .password(passwordEncoder.encode("1234"))
                .email("student02@goorm.io")
                .fullName("학생02")
                .role(UserRole.USER)
                .build();
        userRepository.save(student02);
        log.info("User created: {}", student02.getUsername());

        log.info("=== JPA Data Initialization Completed ===");
        log.info("Login credentials:");
        log.info("  Admin: admin / 1234");
        log.info("  Users: student01, student02 / 1234");
    }
}
