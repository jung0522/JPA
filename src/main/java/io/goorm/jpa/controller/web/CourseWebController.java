package io.goorm.jpa.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Course Web Controller
 * 화면만 제공, 데이터는 API로 조회
 */
@Slf4j
@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseWebController {

    @GetMapping
    public String list(Model model) {
        log.info("Course list page accessed");
        model.addAttribute("pageTitle", "강의");
        return "course/list";
    }

    @GetMapping("/{courseNo}")
    public String detail(@PathVariable Long courseNo, Model model) {
        log.info("Course detail page accessed: courseNo={}", courseNo);
        model.addAttribute("pageTitle", "강의 상세");
        model.addAttribute("courseNo", courseNo);
        return "course/detail";
    }

    @GetMapping("/new")
    public String form(Model model) {
        log.info("Course form page accessed");
        model.addAttribute("pageTitle", "강의 생성");
        return "course/form";
    }

    @GetMapping("/edit/{courseNo}")
    public String editForm(@PathVariable Long courseNo, Model model) {
        log.info("Course edit form page accessed: courseNo={}", courseNo);
        model.addAttribute("pageTitle", "강의 수정");
        model.addAttribute("courseNo", courseNo);
        return "course/form";
    }

    @GetMapping("/my")
    public String myCourses(Model model) {
        log.info("My courses page accessed");
        model.addAttribute("pageTitle", "내 강의");
        return "course/my-list";
    }
}
