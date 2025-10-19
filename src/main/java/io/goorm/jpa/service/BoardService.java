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

import java.time.LocalDateTime;
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

    // ===== 다양한 검색 메서드들 =====

    /**
     * 내용으로 검색
     */
    public Page<BoardResponse> searchByContent(String keyword, Pageable pageable) {
        return boardRepository.findByContentContainingAndDeletedFalse(keyword, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 제목 또는 내용으로 검색
     */
    public Page<BoardResponse> searchByTitleOrContent(String titleKeyword, String contentKeyword, Pageable pageable) {
        return boardRepository.findByTitleContainingOrContentContainingAndDeletedFalse(titleKeyword, contentKeyword, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 작성자명으로 검색
     */
    public Page<BoardResponse> searchByAuthorName(String authorName, Pageable pageable) {
        return boardRepository.findByAuthorFullNameContainingAndDeletedFalse(authorName, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 날짜 범위로 검색
     */
    public Page<BoardResponse> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return boardRepository.findByCreatedAtBetweenAndDeletedFalse(startDate, endDate, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 조회수 이상인 게시글 검색
     */
    public Page<BoardResponse> searchByMinViewCount(Integer minViewCount, Pageable pageable) {
        return boardRepository.findByViewCountGreaterThanEqualAndDeletedFalse(minViewCount, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 조회수 범위로 검색
     */
    public Page<BoardResponse> searchByViewCountRange(Integer minViewCount, Integer maxViewCount, Pageable pageable) {
        return boardRepository.findByViewCountBetweenAndDeletedFalse(minViewCount, maxViewCount, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 복합 검색 - 제목 + 작성자
     */
    public Page<BoardResponse> searchByTitleAndAuthor(String titleKeyword, User author, Pageable pageable) {
        return boardRepository.findByTitleContainingAndAuthorAndDeletedFalse(titleKeyword, author, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 복합 검색 - 제목 + 조회수 이상
     */
    public Page<BoardResponse> searchByTitleAndMinViewCount(String titleKeyword, Integer minViewCount, Pageable pageable) {
        return boardRepository.findByTitleContainingAndViewCountGreaterThanEqualAndDeletedFalse(titleKeyword, minViewCount, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 조회수 내림차순 정렬
     */
    public Page<BoardResponse> getListOrderByViewCountDesc(Pageable pageable) {
        return boardRepository.findByDeletedFalseOrderByViewCountDesc(pageable)
                .map(BoardResponse::from);
    }

    /**
     * 조회수 오름차순 정렬
     */
    public Page<BoardResponse> getListOrderByViewCountAsc(Pageable pageable) {
        return boardRepository.findByDeletedFalseOrderByViewCountAsc(pageable)
                .map(BoardResponse::from);
    }

    /**
     * 인기 게시글 Top N
     */
    public List<BoardResponse> getPopularBoards(Pageable pageable) {
        return boardRepository.findTopByOrderByViewCountDesc(pageable)
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    /**
     * 최신 게시글 Top N
     */
    public List<BoardResponse> getRecentBoards(Pageable pageable) {
        return boardRepository.findTopByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    /**
     * 통합 검색 (제목, 내용, 작성자명)
     */
    public Page<BoardResponse> searchAll(String title, String content, String author, Pageable pageable) {
        return boardRepository.searchByTitleAndContentAndAuthor(title, content, author, pageable)
                .map(BoardResponse::from);
    }

    /**
     * 통계 - 특정 작성자의 게시글 수
     */
    public Long countByAuthor(User author) {
        return boardRepository.countByAuthorAndDeletedFalse(author);
    }

    /**
     * 통계 - 조회수 이상인 게시글 수
     */
    public Long countByMinViewCount(Integer minViewCount) {
        return boardRepository.countByViewCountGreaterThanEqualAndDeletedFalse(minViewCount);
    }

    /**
     * 통계 - 특정 기간 내 게시글 수
     */
    public Long countByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return boardRepository.countByCreatedAtBetweenAndDeletedFalse(startDate, endDate);
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
