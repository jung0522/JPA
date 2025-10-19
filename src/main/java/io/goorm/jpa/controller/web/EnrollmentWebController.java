package io.goorm.jpa.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Enrollment Web Controller
 */
@Slf4j
@Controller
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentWebController {

    @GetMapping("/me")
    public String myEnrollments(Model model) {
        log.info("My enrollments page accessed");
        model.addAttribute("pageTitle", "내 수강신청");
        return "enrollment/my-list";
    }

    @GetMapping("/manage")
    public String manage(Model model) {
        log.info("Enrollment management page accessed");
        model.addAttribute("pageTitle", "수강신청 관리");
        return "enrollment/manage";
    }
}
