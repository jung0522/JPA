package io.goorm.jpa;

import io.goorm.jpa.entity.Comment;
import io.goorm.jpa.entity.Post;
import io.goorm.jpa.repository.CommentRepository;
import io.goorm.jpa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ManyToOne 관계 테스트를 위한 초기 데이터 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 중복 실행 방지
        if (postRepository.count() > 0) {
            log.info("=== 데이터가 이미 존재하여 초기화를 건너뜁니다. ===");
            return;
        }

        log.info("=== ManyToOne 테스트 데이터 초기화 시작 ===");

        // 게시글 3개 생성
        Post post1 = new Post("JPA N+1 문제란?", "JPA에서 가장 흔하게 발생하는 성능 문제입니다.");
        Post post2 = new Post("Fetch Join 완벽 가이드", "Fetch Join을 사용하여 N+1 문제를 해결하는 방법을 알아봅니다.");
        Post post3 = new Post("Spring Data JPA Best Practices", "Spring Data JPA를 사용할 때 알아야 할 모범 사례들입니다.");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // 게시글 1에 댓글 5개
        commentRepository.save(new Comment("매우 유익한 글입니다!", post1));
        commentRepository.save(new Comment("N+1 문제 때문에 고생했는데 도움이 되었습니다.", post1));
        commentRepository.save(new Comment("좀 더 자세한 예제가 있으면 좋겠어요.", post1));
        commentRepository.save(new Comment("실무에서 바로 적용해봤습니다. 감사합니다!", post1));
        commentRepository.save(new Comment("다음 글도 기대하겠습니다.", post1));

        // 게시글 2에 댓글 3개
        commentRepository.save(new Comment("Fetch Join 정말 유용하네요!", post2));
        commentRepository.save(new Comment("페이징 처리할 때 주의사항도 알려주세요.", post2));
        commentRepository.save(new Comment("EntityGraph와 비교해주시면 더 좋을 것 같습니다.", post2));

        // 게시글 3에 댓글 7개
        commentRepository.save(new Comment("Best Practices 정리 감사합니다.", post3));
        commentRepository.save(new Comment("실무에서 꼭 필요한 내용이네요.", post3));
        commentRepository.save(new Comment("BatchSize는 몇으로 설정하는게 좋을까요?", post3));
        commentRepository.save(new Comment("DTO 변환 패턴도 같이 다뤄주셨으면 합니다.", post3));
        commentRepository.save(new Comment("Projection은 언제 사용하나요?", post3));
        commentRepository.save(new Comment("Cascade 옵션 설명도 추가해주세요.", post3));
        commentRepository.save(new Comment("orphanRemoval 차이점이 궁금합니다.", post3));

        log.info("=== 테스트 데이터 초기화 완료: 게시글 3개, 댓글 15개 ===");
    }
}
