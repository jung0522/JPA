# JPA 학습 프로젝트

## 📌 Milestone 0: Backbone (완료)

Spring Boot + JPA 기반 REST API 학습 프로젝트의 **백본(Backbone)** 구조입니다.

---

## 🚀 빠른 시작

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 가이드 페이지 접속
```
http://localhost:8080/guide
```

**📚 모든 사용법과 설정은 가이드 페이지에서 확인하세요!**

---

## 🔧 주요 기능

- ✅ 세션 기반 인증/인가 (Spring Security)
- ✅ JPA Auditing (생성자/수정자 자동 추적)
- ✅ 전역 예외 처리 (API/웹 분리)
- ✅ P6Spy SQL 로깅
- ✅ Swagger/OpenAPI 문서
- ✅ Spring Boot Actuator
- ✅ H2 인메모리 데이터베이스
- ✅ 데이터 초기화 (CommandLineRunner)

---

## 🔐 테스트 계정

| Username | Password | Role |
|----------|----------|------|
| admin | 1234 | ADMIN |
| instructor01 | 1234 | INSTRUCTOR |
| instructor02 | 1234 | INSTRUCTOR |
| student01 | 1234 | STUDENT |
| student02 | 1234 | STUDENT |

---

## 📖 문서

- **가이드 페이지**: http://localhost:8080/guide
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console
- **Actuator**: http://localhost:8080/actuator/health

---

## 🏗️ 기술 스택

**Backend**: Java 21, Spring Boot 3.5.6, Spring Data JPA, Spring Security, Hibernate 6
**Database**: H2 (in-memory), P6Spy
**Security**: Session-based Auth, BCrypt
**Frontend**: Thymeleaf, DaisyUI + Tailwind CSS
**Tools**: Lombok, Validation, Swagger, Actuator, DevTools, QueryDSL

---

## 📁 프로젝트 구조

```
src/main/java/io/goorm/jpa/
├── controller/          # API / 웹 컨트롤러
├── service/             # 비즈니스 로직
├── repository/          # 데이터 접근
├── entity/              # JPA 엔티티
├── dto/                 # 데이터 전송 객체
├── exception/           # 예외 처리
├── config/              # 설정 (Security, JWT, JPA Auditing)
└── enums/               # 열거형

src/main/resources/
├── templates/           # Thymeleaf 템플릿
│   ├── layout/          # 레이아웃
│   ├── guide/           # 가이드 페이지 (8개)
│   └── error/           # 에러 페이지
├── application.properties
├── application-jpa.properties
└── messages*.properties # 다국어 메시지
```

---

## 💡 주요 설계 원칙

1. **계층별 패키지 구조** (Layer-First)
2. **API vs MVC 분리** (controller.api / controller.web)
3. **PK 네이밍 규칙** (userNo, boardNo 등)
4. **BaseEntity 상속** (Auditing, Soft Delete 지원)
5. **전역 예외 처리** (계층별 에러 코드)

---

## 📚 가이드 목록

1. **세션 인증** - Spring Security Session + JPA Auditing 통합
2. **데이터 초기화** - CommandLineRunner + 중복 방지
3. **P6Spy** - SQL 로깅 설정
4. **Actuator** - 모니터링 + 보안 설정
5. **예외 처리** - 전역 예외 핸들러
6. **Swagger** - API 문서화
7. **H2 Console** - 데이터베이스 관리
8. **QueryDSL** - 동적 쿼리 작성

**자세한 내용은 가이드 페이지를 참고하세요!**

---

## 📝 프로젝트 현황

**Step 1 완료**: ManyToOne 단방향 관계 중심

- ✅ 게시판 (Board) CRUD - Query Methods
- ✅ 강의 (Course) 관리 - JPQL + Fetch Join
- ✅ 수강신청 (Enrollment) - QueryDSL + Optimistic Lock
- ✅ 사용자 프로필 (UserProfile) - OneToOne 단방향
- ✅ 페이징 및 검색
- ✅ 권한 기반 접근 제어

**Step 2 예정**: 양방향 관계 및 고급 기능

---

**📚 모든 가이드와 사용법은 http://localhost:8080/guide 에서 확인하세요!**
