package io.goorm.jpa.controller.api;

import io.goorm.jpa.dto.board.BoardCreateRequest;
import io.goorm.jpa.dto.board.BoardResponse;
import io.goorm.jpa.dto.board.BoardUpdateRequest;
import io.goorm.jpa.dto.common.ApiResponse;
import io.goorm.jpa.dto.common.PageResponse;
import io.goorm.jpa.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Board API Controller
 * - Query Methods 사용
 * - ManyToOne 단방향
 */
@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판 API")
public class BoardApiController {

    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "게시글 생성")
    public ApiResponse<BoardResponse> create(@Valid @RequestBody BoardCreateRequest request) {
        BoardResponse response = boardService.create(request);
        return ApiResponse.success(response);
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public ApiResponse<PageResponse<BoardResponse>> getList(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BoardResponse> page;

        if (keyword != null && !keyword.isBlank()) {
            page = boardService.search(keyword, pageable);
        } else {
            page = boardService.getList(pageable);
        }

        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/{boardNo}")
    @Operation(summary = "게시글 상세 조회")
    public ApiResponse<BoardResponse> getDetail(@PathVariable Long boardNo) {
        BoardResponse response = boardService.getDetail(boardNo);
        return ApiResponse.success(response);
    }

    @PutMapping("/{boardNo}")
    @Operation(summary = "게시글 수정")
    public ApiResponse<BoardResponse> update(
            @PathVariable Long boardNo,
            @Valid @RequestBody BoardUpdateRequest request
    ) {
        BoardResponse response = boardService.update(boardNo, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{boardNo}")
    @Operation(summary = "게시글 삭제")
    public ApiResponse<Void> delete(@PathVariable Long boardNo) {
        boardService.delete(boardNo);
        return ApiResponse.success();
    }

    @GetMapping("/my")
    @Operation(summary = "내 게시글 목록")
    public ApiResponse<PageResponse<BoardResponse>> getMyBoards(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BoardResponse> page = boardService.getMyBoards(pageable);
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 Top 10")
    public ApiResponse<List<BoardResponse>> getPopularBoards() {
        List<BoardResponse> response = boardService.getPopularBoards();
        return ApiResponse.success(response);
    }
}
