package io.goorm.jpa.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 가이드 문서 컨트롤러
 * 학습 가이드 및 매뉴얼 페이지 제공
 */
@Slf4j
@Controller
@RequestMapping("/guide")
public class GuideController {

    @GetMapping({"", "/"})
    public String index(Model model) {
        log.info("Guide index page accessed");

        model.addAttribute("title", "JPA 학습 프로젝트");
        model.addAttribute("description", "Spring Boot + JPA 기반 REST API 학습 프로젝트");

        return "guide/index";
    }

    @GetMapping("/actuator")
    public String actuatorGuide(Model model) {
        log.info("Actuator guide page accessed");

        model.addAttribute("pageTitle", "Actuator 모니터링 가이드");

        return "guide/actuator-guide";
    }

    @GetMapping("/exception-handling")
    public String exceptionHandlingGuide(Model model) {
        log.info("Exception handling guide page accessed");

        model.addAttribute("pageTitle", "예외 처리 가이드");

        return "guide/exception-handling-guide";
    }

    @GetMapping("/swagger")
    public String swaggerGuide(Model model) {
        log.info("Swagger guide page accessed");

        model.addAttribute("pageTitle", "Swagger API 문서 가이드");

        return "guide/swagger-guide";
    }

    @GetMapping("/h2-console")
    public String h2ConsoleGuide(Model model) {
        log.info("H2 Console guide page accessed");

        model.addAttribute("pageTitle", "H2 Database Console 가이드");

        return "guide/h2-console-guide";
    }

    @GetMapping("/jwt")
    public String jwtGuide(Model model) {
        log.info("JWT guide page accessed");

        model.addAttribute("pageTitle", "JWT 인증");

        return "guide/jwt-guide";
    }

    @GetMapping("/data-init")
    public String dataInitGuide(Model model) {
        log.info("Data initialization guide page accessed");

        model.addAttribute("pageTitle", "데이터 초기화");

        return "guide/data-init-guide";
    }

    @GetMapping("/p6spy")
    public String p6spyGuide(Model model) {
        log.info("P6Spy guide page accessed");

        model.addAttribute("pageTitle", "P6Spy SQL 로깅");

        return "guide/p6spy-guide";
    }

    @GetMapping("/testing")
    public String testingGuide(Model model) {
        log.info("Testing guide page accessed");

        model.addAttribute("pageTitle", "테스트 가이드");

        return "guide/testing-guide";
    }
}
