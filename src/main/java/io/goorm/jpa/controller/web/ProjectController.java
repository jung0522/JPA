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
}
