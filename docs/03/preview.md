# Step03: JPA 쿼리 방식 완전 가이드

## 📚 학습 목표

### 핵심 개념
- JPQL, Criteria, QueryDSL의 차이점과 특징
- 동적 쿼리의 필요성과 구현 방법
- 각 방식의 장단점과 실무 적용 시나리오

### 실무 능력
- 요구사항에 맞는 쿼리 방식 선택
- 복잡한 검색 조건 처리
- 동적 쿼리 구현 (BooleanExpression 재사용)
- 통계 쿼리 작성

---

## 🎯 전체 구조

### step03/query-jpql
**학습 내용**:
- JPQL 기본 문법
- Join, Fetch Join 활용
- 집계 함수, 서브쿼리
- 동적 쿼리의 한계 (문자열 조합의 위험성)

**실습 시나리오**:
- 댓글 검색 (제목, 내용, 작성일)
- 게시글별 통계 (댓글 수, 최신 댓글)
- 인기 게시글 조회 (댓글 많은 순)
- 동적 검색 조건 (문자열 concat - 안티패턴)

**문제 발견**:
```java
// ❌ 동적 쿼리의 한계
String jpql = "SELECT c FROM Comment c WHERE 1=1 ";
if (keyword != null) {
    jpql += "AND c.content LIKE '%" + keyword + "%' ";  // SQL Injection 위험!
}
if (postId != null) {
    jpql += "AND c.post.id = " + postId + " ";
}
// 타입 안정성 없음, 오타 발견 불가
```

---

### step03/query-criteria
**학습 내용**:
- Criteria API 기본 개념
- CriteriaBuilder, Root, Predicate
- 동적 쿼리 구현 (Predicate 조합)
- 타입 안정성의 장점

**실습 시나리오**:
- step03/query-jpql과 동일한 요구사항
- Criteria로 재구현
- 동적 조건 처리 (Predicate 리스트)

**개선점**:
```java
// ✅ 타입 안정성 확보
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
Root<Comment> root = query.from(Comment.class);

List<Predicate> predicates = new ArrayList<>();
if (keyword != null) {
    predicates.add(cb.like(root.get("content"), "%" + keyword + "%"));
}
if (postId != null) {
    predicates.add(cb.equal(root.get("post").get("id"), postId));
}
query.where(predicates.toArray(new Predicate[0]));
```

**한계점**:
- 코드가 장황하고 가독성 낮음
- 문자열로 필드명 지정 (`"content"`) → 오타 위험 여전히 존재
- 복잡한 쿼리 작성 시 난이도 급상승

---

### step03/query-querydsl
**학습 내용**:
- QueryDSL 설정 (Q클래스 생성)
- 기본 쿼리 작성
- Join, Fetch Join
- 프로젝션 (DTO)
- 동적 쿼리 (BooleanBuilder)

**실습 시나리오**:
- 동일 요구사항을 QueryDSL로 재구현
- Q클래스 활용
- BooleanBuilder로 동적 조건 처리

**장점**:
```java
// ✅ 간결하고 직관적
QComment comment = QComment.comment;
QPost post = QPost.post;

BooleanBuilder builder = new BooleanBuilder();
if (keyword != null) {
    builder.and(comment.content.contains(keyword));
}
if (postId != null) {
    builder.and(comment.post.id.eq(postId));
}

List<Comment> results = queryFactory
    .selectFrom(comment)
    .where(builder)
    .fetch();
```

**다음 단계 예고**:
- BooleanBuilder의 한계 (재사용 불가)
- BooleanExpression의 필요성

---

### step03/query-querydsl-advanced
**학습 내용**:
- BooleanExpression 패턴 (실무 핵심)
- 조건 재사용 및 조합
- 복잡한 검색 조건 처리
- 통계 쿼리 (GroupBy, Having)
- 서브쿼리 활용

**실습 시나리오**:
1. **동적 검색 조건**:
   - 키워드 검색 (제목 + 내용)
   - 작성일 범위 (startDate ~ endDate)
   - 게시글 필터 (postId)
   - 댓글 상태 (status)
   - 정렬 조건 동적 변경
   - 페이징

2. **통계 쿼리**:
   - 게시글별 댓글 수
   - 일별/월별 댓글 통계
   - 인기 게시글 TOP 10
   - 사용자별 활동 통계

3. **복잡한 조인**:
   - Post ← Comment 다중 조건
   - 서브쿼리 (댓글 많은 게시글)

**핵심 패턴: BooleanExpression 재사용**:
```java
// ✅ 조건을 메서드로 분리 → 재사용 가능!
private BooleanExpression contentContains(String keyword) {
    return keyword != null ? comment.content.contains(keyword) : null;
}

private BooleanExpression postIdEq(Long postId) {
    return postId != null ? comment.post.id.eq(postId) : null;
}

private BooleanExpression createdBetween(LocalDate start, LocalDate end) {
    if (start == null && end == null) return null;
    if (start == null) return comment.createdAt.loe(end.atTime(23, 59, 59));
    if (end == null) return comment.createdAt.goe(start.atStartOfDay());
    return comment.createdAt.between(start.atStartOfDay(), end.atTime(23, 59, 59));
}

// 조합해서 사용
List<Comment> results = queryFactory
    .selectFrom(comment)
    .where(
        contentContains(keyword),
        postIdEq(postId),
        createdBetween(startDate, endDate)
    )
    .fetch();
```

**장점**:
- 조건 메서드 재사용 → 중복 제거
- 테스트 가능 (단위 테스트)
- null 처리 자동 (null 반환 시 조건 무시)
- 가독성 극대화

---

## 📊 비교표

| 구분 | JPQL | Criteria | QueryDSL |
|------|------|----------|----------|
| **타입 안정성** | ❌ 문자열 | △ 일부 | ✅ 완전 |
| **가독성** | ✅ 좋음 | ❌ 나쁨 | ✅ 매우 좋음 |
| **동적 쿼리** | ❌ 어려움 | △ 가능 | ✅ 쉬움 |
| **오타 발견** | 런타임 | 런타임 | 컴파일 타임 |
| **IDE 지원** | ❌ 없음 | △ 일부 | ✅ 완전 |
| **학습 곡선** | 쉬움 | 어려움 | 보통 |
| **실무 사용** | 정적 쿼리 | 거의 안 씀 | 동적 쿼리 |

---

## 🎓 학습 순서 (권장)

### 1단계: JPQL 기본 (30분)
- JPQL 문법 익히기
- Join, Fetch Join 차이
- 동적 쿼리 시도 → 한계 발견

### 2단계: Criteria 체험 (20분)
- 동일 요구사항을 Criteria로 구현
- 타입 안정성 확인
- 가독성 문제 체감

### 3단계: QueryDSL 기본 (30분)
- Q클래스 설정
- 기본 쿼리 작성
- BooleanBuilder로 동적 쿼리
- JPQL/Criteria와 비교 → "왜 QueryDSL?"

### 4단계: QueryDSL 고급 (40분)
- BooleanExpression 패턴 익히기
- 복잡한 검색 조건 처리
- 통계 쿼리 작성
- 실무 패턴 체득

**총 120분 (2시간)** 으로 쿼리 방식 완전 정복!

---

## 🚀 실무 권장사항

### 언제 무엇을 사용하나?

#### JPQL
- **정적 쿼리**: 조건이 고정된 경우
- **간단한 조회**: 단순 SELECT + WHERE
- **예시**: 특정 ID로 조회, 전체 목록 조회

```java
@Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
List<Comment> findByPostId(@Param("postId") Long postId);
```

#### Criteria
- **거의 사용 안 함**: QueryDSL로 대체
- **레거시 코드 유지보수**: 기존 Criteria 코드가 있을 때만

#### QueryDSL
- **동적 쿼리**: 검색 조건이 동적으로 변하는 경우
- **복잡한 조건**: 다중 필터, 정렬, 페이징
- **통계 쿼리**: 집계, 그룹핑
- **실무 표준**: 대부분의 복잡한 쿼리

```java
// 실무 패턴
public Page<CommentDto> searchComments(CommentSearchCondition condition, Pageable pageable) {
    return queryFactory
        .select(new QCommentDto(
            comment.id,
            comment.content,
            post.title
        ))
        .from(comment)
        .join(comment.post, post)
        .where(
            contentContains(condition.getKeyword()),
            postIdEq(condition.getPostId()),
            createdBetween(condition.getStartDate(), condition.getEndDate())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(comment.createdAt.desc())
        .fetch();
}
```

---

## 📁 브랜치 구조

```
step03/
├── query-jpql/              # JPQL 기본 + 동적쿼리 한계
│   ├── entity/              # Comment, Post (기존 재사용)
│   ├── repository/          # CommentJpqlRepository
│   ├── service/             # CommentJpqlService
│   └── controller/          # CommentJpqlController
│
├── query-criteria/          # Criteria + 동적쿼리
│   ├── repository/          # CommentCriteriaRepository
│   ├── service/             # CommentCriteriaService
│   └── controller/          # CommentCriteriaController
│
├── query-querydsl/          # QueryDSL 기본
│   ├── config/              # QueryDslConfig
│   ├── repository/          # CommentQueryRepository
│   ├── service/             # CommentQueryService
│   └── controller/          # CommentQueryController
│
└── query-querydsl-advanced/ # BooleanExpression + 통계
    ├── dto/                 # CommentSearchCondition, CommentDto
    ├── repository/          # CommentQueryRepositoryImpl
    ├── service/             # CommentSearchService
    └── controller/          # CommentSearchController
```

---

## 🎯 핵심 포인트

### JPQL → Criteria → QueryDSL 흐름
1. **JPQL**: 기본 개념 이해, 동적 쿼리 한계 체감
2. **Criteria**: 타입 안정성 필요성 이해, 가독성 문제 체감
3. **QueryDSL**: "이게 답이다!" 자연스러운 결론
4. **Advanced**: 실무 패턴 (BooleanExpression) 체득

### 왜 QueryDSL인가?
- **컴파일 타임 오류 발견**: 오타 즉시 확인
- **IDE 자동완성**: 생산성 극대화
- **가독성**: SQL과 유사한 직관적 문법
- **재사용성**: BooleanExpression 패턴
- **유지보수**: 리팩터링 시 타입 체크

---

## 📝 다음 단계

### step04/bidirectional
- 단방향 → 양방향 전환
- 연관관계 편의 메소드
- 양방향 주의사항
- QueryDSL과 양방향 조합

**학습 완료 후 달성 목표**:
- 실무에서 바로 사용 가능한 JPA 쿼리 작성 능력
- 복잡한 검색 조건 처리 능력
- 성능 최적화 (N+1 문제 해결)
- 유지보수 가능한 코드 작성

---

## 🎉 완료!

이제 **JPQL, Criteria, QueryDSL**의 모든 것을 이해하고,
**실무에서 바로 적용 가능한 동적 쿼리 패턴**을 익혔습니다!

**다음**: step04에서 양방향 관계와 편의 메소드를 마스터하면 JPA 완전 정복!
