package io.goorm.jpa.m2m.service;

import io.goorm.jpa.m2m.dto.TagCountDto;
import io.goorm.jpa.m2m.dto.TagCreateRequest;
import io.goorm.jpa.m2m.dto.TagResponse;
import io.goorm.jpa.m2m.entity.Tag;
import io.goorm.jpa.m2m.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    
    private final TagRepository tagRepository;
    
    // R-4: 태그별 게시글 수 집계
    public List<TagCountDto> getTagCounts() {
        List<Object[]> results = tagRepository.findTagCounts();
        return results.stream()
                .map(result -> new TagCountDto(
                    (Long) result[0],
                    (String) result[1],
                    (Long) result[2]
                ))
                .collect(Collectors.toList());
    }
    
    // 태그 생성
    @Transactional
    public TagResponse createTag(TagCreateRequest request) {
        // 중복 체크
        if (tagRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Tag already exists: " + request.name());
        }
        
        Tag tag = new Tag(request.name());
        tag = tagRepository.save(tag);
        return new TagResponse(tag.getId(), tag.getName());
    }
    
    // 태그 조회
    public TagResponse getTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + id));
        return new TagResponse(tag.getId(), tag.getName());
    }
    
    // 태그 목록 조회
    public List<TagResponse> getTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }
    
    // 태그 검색
    public List<TagResponse> searchTags(String keyword) {
        List<Tag> tags = tagRepository.findByNameContaining(keyword);
        return tags.stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }
}
