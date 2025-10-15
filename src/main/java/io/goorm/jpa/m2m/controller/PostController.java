package io.goorm.jpa.m2m.controller;

import io.goorm.jpa.m2m.dto.*;
import io.goorm.jpa.m2m.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/m2m/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    // R-1: 게시글 상세 + 태그 목록
    @GetMapping("/{postId}/tags")
    public ResponseEntity<List<TagResponse>> getTagsByPostId(@PathVariable Long postId) {
        List<TagResponse> tags = postService.getTagsByPostId(postId);
        return ResponseEntity.ok(tags);
    }
    
    // R-2: 태그 상세 + 게시글 목록(페이징)
    @GetMapping("/by-tag/{tagId}")
    public ResponseEntity<Page<PostResponse>> getPostsByTagId(
            @PathVariable Long tagId, 
            Pageable pageable) {
        Page<PostResponse> posts = postService.getPostsByTagId(tagId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    // R-3: 게시글 목록 + 각 게시글의 태그들(프리뷰) - DTO 프로젝션
    @GetMapping("/with-tags")
    public ResponseEntity<Page<PostWithTagsDto>> getPostsWithTags(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<PostWithTagsDto> posts = postService.getPostsWithTags(keyword, pageable);
        return ResponseEntity.ok(posts);
    }
    
    // R-5: 태그명으로 게시글 검색(페이징)
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> getPostsByTagName(
            @RequestParam String tag,
            Pageable pageable) {
        Page<PostResponse> posts = postService.getPostsByTagName(tag, pageable);
        return ResponseEntity.ok(posts);
    }
    
    // C-1: 게시글에 태그 추가
    @PostMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {
        postService.addTagToPost(postId, tagId);
        return ResponseEntity.ok().build();
    }
    
    // C-2: 게시글에서 태그 제거
    @DeleteMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {
        postService.removeTagFromPost(postId, tagId);
        return ResponseEntity.ok().build();
    }
    
    // U-1: 게시글 내 태그 순서 변경
    @PatchMapping("/{postId}/tags/{tagId}/order")
    public ResponseEntity<Void> updateTagOrder(
            @PathVariable Long postId,
            @PathVariable Long tagId,
            @RequestParam Integer order) {
        postService.updateTagOrder(postId, tagId, order);
        return ResponseEntity.ok().build();
    }
    
    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse post = postService.createPost(request);
        return ResponseEntity.ok(post);
    }
    
    // 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }
    
    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(Pageable pageable) {
        Page<PostResponse> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }
}
