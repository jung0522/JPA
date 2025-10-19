package io.goorm.jpa.controller.web;

import io.goorm.jpa.dto.dashboard.DashboardResponse;
import io.goorm.jpa.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹 페이지 홈 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        log.info("Home page accessed");

        DashboardResponse dashboard = dashboardService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("pageTitle", "대시보드");

        return "home";
    }
}
