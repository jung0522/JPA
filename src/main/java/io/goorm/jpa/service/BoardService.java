package io.goorm.jpa.service;

import io.goorm.jpa.dto.board.BoardCreateRequest;
import io.goorm.jpa.dto.board.BoardResponse;
import io.goorm.jpa.dto.board.BoardUpdateRequest;
import io.goorm.jpa.entity.Board;
import io.goorm.jpa.entity.User;
import io.goorm.jpa.exception.BusinessException;
import io.goorm.jpa.exception.ErrorCode;
import io.goorm.jpa.repository.BoardRepository;
import io.goorm.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Board Service
 * - Query Methods 사용
 * - ManyToOne 단방향
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 생성
     */
    @Transactional
    public BoardResponse create(BoardCreateRequest request) {
        User currentUser = getCurrentUser();

        Board board = Board.builder()
                .title(request.title())
                .content(request.content())
                .author(currentUser)
                .build();

        Board savedBoard = boardRepository.save(board);
        log.info("Board created: boardNo={}, author={}", savedBoard.getBoardNo(), currentUser.getUsername());

        return BoardResponse.from(savedBoard);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    public Page<BoardResponse> getList(Pageable pageable) {
        return boardRepository.findByDeletedFalse(pageable)
                .map(BoardResponse::from);
    }

    /**
     * 게시글 검색 (제목, 페이징)
     */
    public Page<BoardResponse> search(String keyword, Pageable pageable) {
        return boardRepository.findByTitleContainingAndDeletedFalse(keyword, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 게시글 상세 조회 (조회수 증가)
     */
    @Transactional
    public BoardResponse getDetail(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (board.getDeleted()) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        // 조회수 증가
        board.increaseViewCount();

        log.info("Board viewed: boardNo={}, viewCount={}", boardNo, board.getViewCount());

        return BoardResponse.from(board);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardResponse update(Long boardNo, BoardUpdateRequest request) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (board.getDeleted()) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        User currentUser = getCurrentUser();

        // 작성자 본인 또는 관리자만 수정 가능
        if (!board.isAuthor(currentUser) && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN);
        }

        board.update(request.title(), request.content());
        log.info("Board updated: boardNo={}, author={}", boardNo, currentUser.getUsername());

        return BoardResponse.from(board);
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    public void delete(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        if (board.getDeleted()) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        User currentUser = getCurrentUser();

        // 작성자 본인 또는 관리자만 삭제 가능
        if (!board.isAuthor(currentUser) && !currentUser.isAdmin()) {
            throw new BusinessException(ErrorCode.BOARD_FORBIDDEN);
        }

        board.delete();
        log.info("Board deleted: boardNo={}, author={}", boardNo, currentUser.getUsername());
    }

    /**
     * 내 게시글 목록 조회
     */
    public Page<BoardResponse> getMyBoards(Pageable pageable) {
        User currentUser = getCurrentUser();

        return boardRepository.findByAuthorAndDeletedFalse(currentUser, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 인기 게시글 Top 10
     */
    public List<BoardResponse> getPopularBoards() {
        return boardRepository.findTop10ByOrderByViewCountDesc(Pageable.ofSize(10))
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    /**
     * 현재 로그인한 사용자 조회
     */
    private User getCurrentUser() {
        String userNo = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
