package io.goorm.jpa.m2m.controller;

import io.goorm.jpa.m2m.dto.TagCountDto;
import io.goorm.jpa.m2m.dto.TagCreateRequest;
import io.goorm.jpa.m2m.dto.TagResponse;
import io.goorm.jpa.m2m.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/m2m/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    // R-4: 태그별 게시글 수 집계
    @GetMapping("/counts")
    public ResponseEntity<List<TagCountDto>> getTagCounts() {
        List<TagCountDto> counts = tagService.getTagCounts();
        return ResponseEntity.ok(counts);
    }
    
    // 태그 생성
    @PostMapping
    public ResponseEntity<TagResponse> createTag(@RequestBody TagCreateRequest request) {
        TagResponse tag = tagService.createTag(request);
        return ResponseEntity.ok(tag);
    }
    
    // 태그 조회
    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTag(@PathVariable Long id) {
        TagResponse tag = tagService.getTag(id);
        return ResponseEntity.ok(tag);
    }
    
    // 태그 목록 조회
    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags() {
        List<TagResponse> tags = tagService.getTags();
        return ResponseEntity.ok(tags);
    }
    
    // 태그 검색
    @GetMapping("/search")
    public ResponseEntity<List<TagResponse>> searchTags(@RequestParam String keyword) {
        List<TagResponse> tags = tagService.searchTags(keyword);
        return ResponseEntity.ok(tags);
    }
}
