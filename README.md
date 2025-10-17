# JPA ManyToOne 관계 학습 프로젝트

## 📌 프로젝트 개요

JPA N+1 문제와 해결 방법을 학습하기 위한 Spring Boot 프로젝트입니다.

**주제**: 게시글(Post) - 댓글(Comment) 관계 (ManyToOne)

### 학습 목표
1. N+1 문제 발생 원인 이해
2. JOIN vs FETCH JOIN 차이 이해
3. @EntityGraph, @BatchSize 활용법
4. Entity vs DTO 반환 방식 비교
5. Cascade & orphanRemoval 실전 적용

---

## 🚀 시작하기

### 1. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 2. Swagger UI 접속

```
http://localhost:8080/swagger-ui.html
```

### 3. 데이터베이스

- **DB**: MariaDB
- **Port**: 3307
- **Database**: goorm_jpa
- **초기 데이터**: 게시글 3개, 댓글 15개 자동 생성

---

## 📡 API 테스트 시나리오

### 1단계: N+1 문제 발생

**API 호출**
```http
GET http://localhost:8080/api/posts/n-plus-1
```

**콘솔 로그 확인 포인트**
```sql
-- 1. 게시글 조회 쿼리 (1번)
SELECT * FROM posts;

-- 2. 각 게시글마다 댓글 조회 쿼리 (N번 - 3번 발생)
SELECT * FROM comments WHERE post_id = 1;
SELECT * FROM comments WHERE post_id = 2;
SELECT * FROM comments WHERE post_id = 3;
```

**확인할 내용**
- 총 **4번 쿼리** 발생 (1 + 3)
- 게시글 100개면 101번 쿼리!
- 이것이 N+1 문제의 심각성

---

### 2단계: 일반 JOIN (N+1 여전히 발생)

**API 호출**
```http
GET http://localhost:8080/api/posts/join
```

**콘솔 로그 확인 포인트**
```sql
-- 1. 게시글만 조회 (JOIN은 했지만 Comment는 SELECT 안 됨)
SELECT p.*
FROM tb_m2o_post p
LEFT JOIN tb_m2o_comment c ON p.post_id = c.post_id;

-- 2. 각 게시글마다 댓글 조회 쿼리 (N번 - 여전히 발생!)
SELECT * FROM tb_m2o_comment WHERE post_id = 1;
SELECT * FROM tb_m2o_comment WHERE post_id = 2;
SELECT * FROM tb_m2o_comment WHERE post_id = 3;
```

**확인할 내용**
- JOIN 명시했지만 여전히 **4번 쿼리** 발생!
- 일반 JOIN: 조건 필터링용 (WHERE, GROUP BY 등)
- **FETCH 키워드가 없으면 데이터 로딩 안 됨!**

**핵심 포인트**
- **일반 JOIN ≠ FETCH JOIN** (가장 중요!)
- JOIN: "조건만 걸어줘" (데이터는 안 가져옴)
- FETCH JOIN: "데이터까지 다 가져와!" (한 번에 로딩)

---

### 3단계: Fetch Join 해결

**API 호출**
```http
GET http://localhost:8080/api/posts/fetch-join
```

**콘솔 로그 확인 포인트**
```sql
-- 1번의 쿼리로 모두 조회 (FETCH가 핵심!)
SELECT p.*, c.*
FROM tb_m2o_post p
LEFT JOIN tb_m2o_comment c ON p.post_id = c.post_id;
```

**확인할 내용**
- 단 **1번의 쿼리**로 모든 데이터 조회
- FETCH 키워드가 핵심: 연관 엔티티를 함께 조회
- 2단계 일반 JOIN과 비교: SELECT 절에 `c.*`가 포함됨!

---

### 4단계: @EntityGraph 해결

**API 호출**
```http
GET http://localhost:8080/api/posts/entity-graph
```

**콘솔 로그 확인 포인트**
```sql
-- Fetch Join과 동일한 결과
SELECT p.*, c.*
FROM tb_m2o_post p
LEFT JOIN tb_m2o_comment c ON p.post_id = c.post_id;
```

**확인할 내용**
- JPQL 없이 어노테이션만으로 Fetch Join 효과
- 간단한 경우에는 @EntityGraph가 더 편리
- 복잡한 조건이 필요하면 Fetch Join 사용

---

### 5단계: @BatchSize 해결

**API 호출**
```http
GET http://localhost:8080/api/posts/batch-size
```

**콘솔 로그 확인 포인트**
```sql
-- 1. 게시글 조회 (1번)
SELECT * FROM tb_m2o_post;

-- 2. 댓글 배치 조회 (IN 절 사용, 1번)
SELECT * FROM tb_m2o_comment
WHERE post_id IN (1, 2, 3);  -- 한 번에 조회!
```

**확인할 내용**
- N+1을 1+1로 개선 (총 **2번 쿼리**)
- IN 절로 한 번에 여러 ID 조회
- size=10: 최대 10개씩 묶어서 조회
- LAZY 로딩이지만 성능 최적화

---

### 6단계: Entity 직접 반환 문제

**API 호출**
```http
GET http://localhost:8080/api/comments/1/entity
```

**콘솔 로그 확인 포인트**
```
========================================
❌ Entity 직접 반환 에러 발생!
========================================
에러 타입: JsonMappingException
원인: Entity를 직접 JSON으로 변환하려 했습니다.
LAZY 프록시 객체(ByteBuddyInterceptor)는 직렬화할 수 없습니다!
========================================
해결책: DTO로 변환해서 반환하세요!
========================================
```

**확인할 내용**
- Entity를 직접 반환하면 안 되는 이유
- LAZY 로딩: Hibernate Proxy 객체 생성
- Jackson이 Proxy를 JSON으로 변환 불가
- **해결책: DTO 변환 필수!**

---

### 7단계: DTO 반환 (정상)

**API 호출**
```http
GET http://localhost:8080/api/posts/1/dto
```

**응답 확인**
```json
{
  "id": 1,
  "title": "JPA N+1 문제란?",
  "content": "JPA에서 가장 흔하게 발생하는 성능 문제입니다.",
  "comments": [
    {
      "id": 1,
      "content": "매우 유익한 글입니다!",
      "createdAt": "2025-10-17T..."
    }
  ]
}
```

**확인할 내용**
- DTO로 변환하면 정상 작동
- Entity → DTO 변환 시점에 데이터 로딩
- API 응답은 항상 DTO 사용 필수

---

### 8단계: 페이징 처리

**API 호출**
```http
GET http://localhost:8080/api/posts?page=0&size=2&sort=id,desc
```

**응답 확인**
```json
{
  "content": [ ... ],
  "pageable": { "pageNumber": 0, "pageSize": 2 },
  "totalElements": 3,
  "totalPages": 2
}
```

**확인할 내용**
- Spring Data JPA Pageable 자동 처리
- page, size, sort 파라미터 지원
- Page<T>는 페이징 메타데이터 포함

---

### 9단계: Cascade.PERSIST (게시글 생성)

**API 호출**
```http
POST http://localhost:8080/api/posts
Content-Type: application/json

{
  "title": "새 게시글",
  "content": "내용입니다.",
  "commentContents": [
    "첫 번째 댓글",
    "두 번째 댓글"
  ]
}
```

**콘솔 로그 확인 포인트**
```sql
-- 1. 게시글 INSERT
INSERT INTO tb_m2o_post (post_title, post_content) VALUES (...);

-- 2. 댓글 자동 INSERT (Cascade 효과)
INSERT INTO tb_m2o_comment (comment_content, post_id, created_at) VALUES (...);
INSERT INTO tb_m2o_comment (comment_content, post_id, created_at) VALUES (...);
```

**확인할 내용**
- Cascade.PERSIST: 부모 저장 시 자식도 자동 저장
- commentRepository.save() 호출 불필요
- 연관관계 편의성 제공

---

### 10단계: Cascade.REMOVE (게시글 삭제)

**먼저 ID 확인**
```http
GET http://localhost:8080/api/posts
```

**API 호출**
```http
DELETE http://localhost:8080/api/posts/4
```

**콘솔 로그 확인 포인트**
```sql
-- 1. 댓글 자동 DELETE (Cascade 효과)
DELETE FROM tb_m2o_comment WHERE post_id = 4;

-- 2. 게시글 DELETE
DELETE FROM tb_m2o_post WHERE post_id = 4;
```

**확인할 내용**
- Cascade.REMOVE: 부모 삭제 시 자식도 자동 삭제
- FK 제약조건 위반 방지
- 데이터 정합성 유지

---

### 11단계: orphanRemoval (댓글 삭제)

**댓글 ID 확인**
```http
GET http://localhost:8080/api/posts/2/dto
```

**API 호출**
```http
DELETE http://localhost:8080/api/posts/comments/6
```

**콘솔 로그 확인 포인트**
```sql
-- Post에서 Comment 제거 → 자동 DELETE!
DELETE FROM tb_m2o_comment WHERE comment_id = 6;
```

**확인할 내용**
- orphanRemoval: 고아 객체 자동 제거
- 부모와의 관계가 끊어지면 자동 삭제
- **Cascade.REMOVE와 차이점:**
  - Cascade.REMOVE: 부모 삭제 시
  - orphanRemoval: 관계 끊김 시 (컬렉션에서 제거)

---

## 📊 N+1 해결 방법 비교

| 방법 | 쿼리 수 | 사용 시점 | 장점 | 단점 |
|------|---------|-----------|------|------|
| **일반 JOIN** | 1+N | 조건 필터링만 | WHERE 조건 가능 | ❌ N+1 해결 안 됨 |
| **Fetch Join** | 1 | 복잡한 조건 | ✅ 한 번에 조회 | JPQL 작성 필요 |
| **@EntityGraph** | 1 | 간단한 경우 | ✅ 어노테이션만 | 복잡한 조건 어려움 |
| **@BatchSize** | 2 | LAZY 유지 필요 | ✅ LAZY + 최적화 | 여전히 2번 쿼리 |

---

## 🎯 Cascade vs orphanRemoval

| 구분 | Cascade.REMOVE | orphanRemoval |
|------|----------------|---------------|
| **삭제 시점** | 부모 삭제 시 | 관계 끊김 시 |
| **사용 예** | 게시글 삭제 → 댓글 삭제 | 컬렉션에서 제거 → 삭제 |
| **설정** | `cascade = CascadeType.REMOVE` | `orphanRemoval = true` |

---

## 💡 핵심 정리

### 1. N+1 문제
- **원인**: LAZY 로딩으로 인한 반복 쿼리 발생
- **해결**: Fetch Join (1번), @EntityGraph (1번), @BatchSize (2번)

### 2. JOIN vs FETCH JOIN
- **일반 JOIN**: 조건만, 데이터 로딩 안 됨 → N+1 발생
- **FETCH JOIN**: 데이터까지 로딩 → N+1 해결
- **핵심**: FETCH 키워드가 있어야 연관 엔티티를 가져옴!

### 3. Entity vs DTO
- **Entity 직접 반환**: LAZY 프록시 직렬화 불가 → 에러
- **DTO 반환**: 안전, 권장
- **결론**: 연관관계 있으면 DTO 필수!

### 4. Cascade
- **PERSIST**: 부모 저장 시 자식도 저장
- **REMOVE**: 부모 삭제 시 자식도 삭제
- **orphanRemoval**: 관계 끊으면 자동 삭제

### 5. 페이징
- **Pageable**: page, size, sort 자동 처리
- **FETCH JOIN + 페이징**: OneToMany에서 위험 (메모리)
- **@BatchSize + 페이징**: 안전

---

## 🔧 기술 스택

- Java 21
- Spring Boot 3.5.6
- Spring Data JPA
- Hibernate 6
- MariaDB
- Lombok
- Swagger/OpenAPI 3

---

## 📞 문의

문제가 발생하면 이슈를 등록해주세요!
