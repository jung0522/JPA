package io.goorm.jpa.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹 페이지 홈 컨트롤러
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        log.info("Home page accessed");
        model.addAttribute("pageTitle", "홈");
        return "home";
    }
}
