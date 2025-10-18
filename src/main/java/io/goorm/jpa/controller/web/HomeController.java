package io.goorm.jpa.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹 페이지 홈 컨트롤러
 * 메인 페이지는 가이드 인덱스로 리다이렉트
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        log.info("Home page accessed - redirecting to guide");
        return "redirect:/guide";
    }
}
