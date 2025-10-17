package io.goorm.jpa.m2m;

import io.goorm.jpa.m2m.entity.Post;
import io.goorm.jpa.m2m.entity.PostTag;
import io.goorm.jpa.m2m.entity.Tag;
import io.goorm.jpa.m2m.repository.PostRepository;
import io.goorm.jpa.m2m.repository.PostTagRepository;
import io.goorm.jpa.m2m.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 데이터 있으면 초기화 건너뛰기
        if (tagRepository.count() > 0) {
            log.info("데이터가 이미 존재하여 초기화를 건너뜁니다.");
            return;
        }

        log.info("=== M2M 테스트 데이터 초기화 시작 ===");

        // 1. 태그 생성
        Tag javaTag = tagRepository.save(new Tag("Java"));
        Tag springTag = tagRepository.save(new Tag("Spring"));
        Tag jpaTag = tagRepository.save(new Tag("JPA"));
        Tag databaseTag = tagRepository.save(new Tag("Database"));

        // 2. 게시글 생성
        Post post1 = postRepository.save(new Post("JPA 다대다 관계 정리", "JPA에서 다대다 관계를 올바르게 구현하는 방법을 정리합니다."));
        Post post2 = postRepository.save(new Post("Spring Boot 시작하기", "Spring Boot로 웹 애플리케이션을 만드는 방법을 알아봅니다."));
        Post post3 = postRepository.save(new Post("데이터베이스 설계 원칙", "좋은 데이터베이스를 설계하는 원칙들을 살펴봅니다."));

        // 3. 중간 엔티티(PostTag) 생성 - 게시글과 태그 연결
        // post1: Java, JPA, Database 태그
        PostTag pt1 = new PostTag(post1, javaTag);
        PostTag pt2 = new PostTag(post1, jpaTag);
        PostTag pt3 = new PostTag(post1, databaseTag);

        // post2: Java, Spring 태그
        PostTag pt4 = new PostTag(post2, javaTag);
        PostTag pt5 = new PostTag(post2, springTag);

        // post3: Database 태그
        PostTag pt6 = new PostTag(post3, databaseTag);

        // 저장
        List<PostTag> postTags = List.of(pt1, pt2, pt3, pt4, pt5, pt6);
        postTagRepository.saveAll(postTags);

        log.info("=== 데이터 초기화 완료 ===");
        log.info("생성된 태그 수: {}", tagRepository.count());
        log.info("생성된 게시글 수: {}", postRepository.count());
        log.info("생성된 관계 수: {}", postTagRepository.count());
    }
}
