package io.goorm.jpa.m2m.service;

import io.goorm.jpa.m2m.dto.*;
import io.goorm.jpa.m2m.entity.Post;
import io.goorm.jpa.m2m.entity.PostTag;
import io.goorm.jpa.m2m.entity.Tag;
import io.goorm.jpa.m2m.repository.PostRepository;
import io.goorm.jpa.m2m.repository.PostTagRepository;
import io.goorm.jpa.m2m.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    
    // R-1: 게시글 상세 + 태그 목록
    public List<TagResponse> getTagsByPostId(Long postId) {
        List<Tag> tags = postTagRepository.findTagsByPostId(postId);
        return tags.stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }
    
    // R-2: 태그 상세 + 게시글 목록(페이징)
    public Page<PostResponse> getPostsByTagId(Long tagId, Pageable pageable) {
        Page<Post> posts = postTagRepository.findPostsByTagId(tagId, pageable);
        return posts.map(this::toPostResponse);
    }
    
    // R-3: 게시글 목록 + 각 게시글의 태그들(프리뷰) - DTO 프로젝션
    public Page<PostWithTagsDto> getPostsWithTags(String keyword, Pageable pageable) {
        return postTagRepository.findPostsWithTags(keyword, pageable);
    }
    
    // R-5: 태그명으로 게시글 검색(페이징)
    public Page<PostResponse> getPostsByTagName(String tagName, Pageable pageable) {
        Page<Post> posts = postTagRepository.findPostsByTagName(tagName, pageable);
        return posts.map(this::toPostResponse);
    }
    
    // C-1: 게시글에 태그 추가
    @Transactional
    public void addTagToPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagId));
        
        // 중복 체크
        if (postTagRepository.findByPostIdAndTagId(postId, tagId).isPresent()) {
            throw new IllegalArgumentException("Tag already exists for this post");
        }
        
        PostTag postTag = new PostTag(post, tag);
        postTagRepository.save(postTag);
    }
    
    // C-2: 게시글에서 태그 제거
    @Transactional
    public void removeTagFromPost(Long postId, Long tagId) {
        postTagRepository.deleteByPostIdAndTagId(postId, tagId);
    }
    
    // U-1: 게시글 내 태그 순서 변경
    @Transactional
    public void updateTagOrder(Long postId, Long tagId, Integer newOrder) {
        PostTag postTag = postTagRepository.findByPostIdAndTagId(postId, tagId)
                .orElseThrow(() -> new IllegalArgumentException("PostTag not found"));
        postTag.updateOrder(newOrder);
    }
    
    // 게시글 생성 (태그와 함께)
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());
        post = postRepository.save(post);
        
        // 태그 연결
        if (request.tagNames() != null && !request.tagNames().isEmpty()) {
            List<Tag> tags = tagRepository.findByNameIn(request.tagNames());
            for (Tag tag : tags) {
                PostTag postTag = new PostTag(post, tag);
                postTagRepository.save(postTag);
            }
        }
        
        return toPostResponse(post);
    }
    
    // 게시글 조회
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
        return toPostResponse(post);
    }
    
    // 게시글 목록 조회
    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(this::toPostResponse);
    }
    
    private PostResponse toPostResponse(Post post) {
        List<TagResponse> tags = post.getPostTags().stream()
                .map(postTag -> new TagResponse(
                    postTag.getTag().getId(), 
                    postTag.getTag().getName()
                ))
                .collect(Collectors.toList());
        
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            tags,
            null // createdAt은 BaseEntity가 없어서 null
        );
    }
}
