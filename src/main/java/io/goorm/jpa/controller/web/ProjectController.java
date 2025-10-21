package io.goorm.jpa.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 프로젝트 문서 컨트롤러
 * Step별 구현 현황 및 API 엔드포인트 문서 제공
 */
@Slf4j
@Controller
@RequestMapping("/project")
public class ProjectController {

    @GetMapping({"", "/"})
    public String index(Model model) {
        log.info("Project index page accessed");
        model.addAttribute("title", "학사관리 시스템");
        model.addAttribute("description", "Step별 구현 현황 및 API 엔드포인트");
        return "project/index";
    }

    @GetMapping("/summary")
    public String summary(Model model) {
        log.info("Project summary page accessed");
        model.addAttribute("pageTitle", "프로젝트 설계");
        return "project/summary";
    }

    @GetMapping("/step1/overview")
    public String step1Overview(Model model) {
        log.info("Step 1 overview page accessed");
        model.addAttribute("pageTitle", "Step 1 구현 내용");
        return "project/step1/overview";
    }

    @GetMapping("/step1/querydsl-setup")
    public String step1QuerydslSetup(Model model) {
        log.info("Step 1 QueryDSL setup page accessed");
        model.addAttribute("pageTitle", "QueryDSL 설정");
        return "project/step1/querydsl-setup";
    }

    @GetMapping("/step1/querydsl-theory")
    public String step1QuerydslTheory(Model model) {
        log.info("Step 1 QueryDSL theory page accessed");
        model.addAttribute("pageTitle", "QueryDSL 기본 이론");
        return "project/step1/querydsl-theory";
    }


    @GetMapping("/auth-session")
    public String authSession(Model model) {
        log.info("Auth session document page accessed");
        model.addAttribute("pageTitle", "인증 방식: JWT → 세션 전환");
        return "project/auth-session";
    }
}
