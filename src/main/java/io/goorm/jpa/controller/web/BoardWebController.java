package io.goorm.jpa.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Board Web Controller
 * 화면만 제공, 데이터는 API로 조회
 */
@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardWebController {

    @GetMapping
    public String list(Model model) {
        log.info("Board list page accessed");
        model.addAttribute("pageTitle", "게시판");
        return "board/list";
    }

    @GetMapping("/{boardNo}")
    public String detail(@PathVariable Long boardNo, Model model) {
        log.info("Board detail page accessed: boardNo={}", boardNo);
        model.addAttribute("pageTitle", "게시글 상세");
        model.addAttribute("boardNo", boardNo);
        return "board/detail";
    }

    @GetMapping("/new")
    public String form(Model model) {
        log.info("Board form page accessed");
        model.addAttribute("pageTitle", "게시글 작성");
        return "board/form";
    }

    @GetMapping("/edit/{boardNo}")
    public String editForm(@PathVariable Long boardNo, Model model) {
        log.info("Board edit form page accessed: boardNo={}", boardNo);
        model.addAttribute("pageTitle", "게시글 수정");
        model.addAttribute("boardNo", boardNo);
        return "board/form";
    }

    @GetMapping("/my")
    public String myBoards(Model model) {
        log.info("My boards page accessed");
        model.addAttribute("pageTitle", "내 게시글");
        return "board/my-list";
    }
}
