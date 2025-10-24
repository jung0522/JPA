# 🧪 사용자 테스트 시나리오

## 📋 개요
JPA 학습 프로젝트의 **Step 1**과 **Step 2** 기능을 체계적으로 테스트할 수 있는 시나리오입니다.

---

## 🔐 테스트 계정

| Username | Password | Role | 설명 |
|----------|----------|------|------|
| admin | 1234 | ADMIN | 관리자 (모든 권한) |
| instructor01 | 1234 | INSTRUCTOR | 강사 (강의 생성/관리) |
| instructor02 | 1234 | INSTRUCTOR | 강사 (강의 생성/관리) |
| student01 | 1234 | STUDENT | 학생 (수강신청) |
| student02 | 1234 | STUDENT | 학생 (수강신청) |

---

## 🎯 Step 1 테스트 시나리오

### **1.1 기본 인증 테스트**
1. **로그인 테스트**
   - 각 계정으로 로그인 시도
   - 잘못된 비밀번호로 로그인 실패 테스트
   - 로그아웃 테스트

2. **권한별 접근 테스트**
   - 학생: 강의 조회, 수강신청만 가능
   - 강사: 강의 생성/수정/삭제 가능
   - 관리자: 모든 기능 접근 가능

### **1.2 게시판 기능 테스트**
1. **게시글 CRUD**
   - 게시글 작성 (제목, 내용 입력)
   - 게시글 목록 조회 (페이징 확인)
   - 게시글 상세 조회 (조회수 증가 확인)
   - 게시글 수정 (작성자만 가능)
   - 게시글 삭제 (작성자만 가능)

2. **게시글 검색**
   - 제목으로 검색
   - 내용으로 검색
   - 작성자명으로 검색
   - 날짜 범위 검색

### **1.3 강의 관리 테스트**
1. **강의 CRUD (강사 계정)**
   - 강의 생성 (강의명, 설명, 최대 인원)
   - 강의 목록 조회 (강사 정보 포함)
   - 강의 상세 조회
   - 강의 수정
   - 강의 삭제

2. **강의 검색**
   - 수강 가능한 강의 검색
   - 키워드로 강의 검색

### **1.4 수강신청 테스트**
1. **수강신청 프로세스**
   - 학생 계정으로 수강신청
   - 중복 수강신청 방지 테스트
   - 정원 초과 시 신청 불가 테스트

2. **수강신청 관리**
   - 내 수강신청 목록 조회
   - 수강신청 취소 (대기 상태만)
   - 수강신청 승인/거절 (강사/관리자)

### **1.5 커리큘럼 관리 테스트**
1. **커리큘럼 CRUD**
   - 강의별 커리큘럼 추가
   - 주차별 커리큘럼 조회
   - 커리큘럼 수정/삭제

---

## 🚀 Step 2 테스트 시나리오

### **2.1 양방향 관계 테스트**
1. **Course ↔ Curriculum 양방향**
   - 강의에서 커리큘럼 직접 추가: `course.addCurriculum()`
   - 강의에서 커리큘럼 개수 확인: `course.getCurriculumCount()`
   - 커리큘럼에서 강의 설정: `curriculum.setCourse()`

2. **Course ↔ Enrollment 양방향**
   - 강의에서 수강생 목록 조회: `course.getEnrollments()`
   - 강의에서 실제 수강생 수: `course.getActualStudentCount()`
   - 강의에서 특정 학생 수강 여부: `course.isEnrolledBy()`

3. **User ↔ Enrollment 양방향**
   - 사용자에서 수강신청 목록: `user.getEnrollments()`
   - 사용자에서 수강신청 개수: `user.getEnrollmentCount()`
   - 사용자에서 특정 강의 수강 여부: `user.isEnrolledIn()`

### **2.2 편의 메서드 테스트**
1. **Course 편의 메서드**
   ```java
   // 커리큘럼 관리
   course.addCurriculum(curriculum);
   course.removeCurriculum(curriculum);
   course.getCurriculumCount();
   course.hasCurriculums();
   
   // 수강신청 관리
   course.addEnrollment(enrollment);
   course.removeEnrollment(enrollment);
   course.getActualStudentCount();
   course.isEnrolledBy(student);
   ```

2. **User 편의 메서드**
   ```java
   // 수강신청 관리
   user.addEnrollment(enrollment);
   user.removeEnrollment(enrollment);
   user.getEnrollmentCount();
   user.isEnrolledIn(course);
   user.getApprovedEnrollments();
   ```

### **2.3 비관적 락 테스트**
1. **동시성 테스트**
   - 두 사용자가 동시에 같은 강의 수정 시도
   - 두 사용자가 동시에 같은 수강신청 승인 시도
   - 락 대기 시간 확인

2. **배치 처리 테스트**
   - 여러 수강신청 동시 승인/거절
   - 강의별 수강신청 일괄 처리

### **2.4 QueryDSL 고급 기능 테스트**
1. **통계 쿼리**
   - 강의 전체 통계 조회
   - 강사별 강의 통계
   - 월별 강의 개설 통계
   - 강의별 커리큘럼 통계

2. **동적 검색**
   - 강의명, 강사명, 설명으로 검색
   - 정원 범위 검색
   - 수강 가능 여부 필터링
   - 수강 비율 범위 검색

3. **고급 검색**
   - 복합 조건 검색
   - 정렬 옵션 테스트
   - 페이징 테스트

### **2.5 배치 처리 테스트**
1. **일반 배치 처리**
   - 여러 수강신청 선택하여 일괄 승인
   - 여러 수강신청 선택하여 일괄 거절
   - 처리 결과 확인 (성공/실패 개수)

2. **강의별 배치 처리**
   - 특정 강의의 모든 대기 중인 수강신청 일괄 승인
   - 특정 강의의 모든 대기 중인 수강신청 일괄 거절

---

## 🔍 API 테스트 (Postman/curl)

### **Step 1 API 테스트**
```bash
# 1. 로그인
POST /api/auth/login
{
  "username": "student01",
  "password": "1234"
}

# 2. 강의 목록 조회
GET /api/courses?page=0&size=10

# 3. 수강신청
POST /api/enrollments
{
  "courseNo": 1
}

# 4. 내 수강신청 목록
GET /api/enrollments/my
```

### **Step 2 API 테스트**
```bash
# 1. 강의 통계
GET /api/courses/statistics

# 2. 강의 고급 검색
POST /api/courses/search
{
  "courseName": "Spring",
  "minCapacity": 10,
  "maxCapacity": 50,
  "isAvailable": true
}

# 3. 수강신청 일괄 승인
POST /api/enrollments/batch
{
  "enrollmentIds": [1, 2, 3],
  "action": "APPROVE",
  "reason": "일괄 승인 처리"
}

# 4. 강의별 수강신청 일괄 처리
POST /api/enrollments/batch/course/1?action=APPROVE&reason=강의별_일괄_승인
```

---

## 🐛 에러 시나리오 테스트

### **Step 1 에러 테스트**
1. **권한 에러**
   - 학생이 강의 생성 시도 → 403 Forbidden
   - 다른 사용자 게시글 수정 시도 → 403 Forbidden

2. **비즈니스 에러**
   - 중복 수강신청 → 409 Conflict
   - 정원 초과 수강신청 → 409 Conflict
   - 존재하지 않는 강의 수강신청 → 404 Not Found

3. **검증 에러**
   - 빈 제목으로 게시글 작성 → 400 Bad Request
   - 잘못된 이메일 형식 → 400 Bad Request

### **Step 2 에러 테스트**
1. **락킹 에러**
   - 동시 수정 시도 → 409 Conflict (Optimistic Lock)
   - 락 대기 시간 초과 → 409 Conflict (Pessimistic Lock)

2. **배치 처리 에러**
   - 빈 수강신청 목록으로 배치 처리 → 400 Bad Request
   - 이미 처리된 수강신청 재처리 → 400 Bad Request

---

## 📊 성능 테스트

### **N+1 문제 확인**
1. **Step 1에서 N+1 발생 확인**
   - 강의 목록 조회 후 각 강의의 강사 정보 접근
   - 수강신청 목록 조회 후 각 수강신청의 학생/강의 정보 접근

2. **Step 2에서 N+1 해결 확인**
   - `@EntityGraph` 적용 후 쿼리 개수 확인
   - `default_batch_fetch_size` 설정 효과 확인

### **동시성 테스트**
1. **Optimistic Lock 테스트**
   - 동시 수강신청 시도
   - 재시도 로직 동작 확인

2. **Pessimistic Lock 테스트**
   - 동시 강의 수정 시도
   - 락 대기 및 순차 처리 확인

---

## ✅ 체크리스트

### **Step 1 완료 체크**
- [ ] 모든 CRUD 기능 정상 동작
- [ ] 권한별 접근 제어 정상
- [ ] 검색 및 페이징 정상
- [ ] 예외 처리 정상
- [ ] N+1 문제 발생 확인

### **Step 2 완료 체크**
- [ ] 양방향 관계 정상 동작
- [ ] 편의 메서드 정상 동작
- [ ] 비관적 락 정상 동작
- [ ] QueryDSL 통계 쿼리 정상
- [ ] 배치 처리 정상 동작
- [ ] N+1 문제 해결 확인
- [ ] 성능 최적화 효과 확인

---

## 🚧 화면에서 테스트 불가능한 API 엔드포인트 (Postman/curl로만 테스트 가능)

### **🔧 Step 2 배치 처리 API (구현됨, UI 없음)**
```bash
# 1. 수강신청 일괄 승인/거절
POST /api/enrollments/batch
{
  "enrollmentIds": [1, 2, 3],
  "action": "APPROVE",
  "reason": "일괄 승인 처리"
}

# 2. 강의별 수강신청 일괄 승인/거절
POST /api/enrollments/batch/course/1?action=APPROVE&reason=강의별_일괄_승인
```

### **📊 Step 2 통계 API (Service 구현됨, Controller 없음)**
```bash
# 강의 통계 (CourseService에 구현됨, API 엔드포인트 없음)
# - getCourseStatistics() - 강의 전체 통계
# - getCourseDetailedStatistics() - 강의별 상세 통계  
# - getInstructorStatistics() - 강사별 강의 통계
# - getMonthlyCourseStatistics() - 월별 강의 개설 통계
# - getCourseCurriculumStatistics() - 강의별 커리큘럼 통계

# 강의 고급 검색 (CourseService에 구현됨, API 엔드포인트 없음)
# - searchCourses(CourseSearchCondition) - 복합 조건 검색
# - getPopularCourses(int limit) - 인기 강의 TOP N
# - getAvailableCourses() - 수강 가능한 강의 목록
```

### **🎯 Step 1 팝업 기능 (Service 구현됨, API 없음)**
```bash
# 강의 커리큘럼 조회 (팝업용)
# - getCurriculums(Long courseNo) - 강의별 커리큘럼 목록

# 강의 수강생 목록 조회 (팝업용)  
# - getEnrolledStudents(Long courseNo) - 강의별 수강생 목록
```

### **📱 화면에서 테스트 가능한 기능**

#### **✅ Step 1 웹 화면**
- 로그인/로그아웃
- 게시판 CRUD (목록, 상세, 작성, 수정, 삭제)
- 게시판 검색 (제목, 내용, 작성자)
- 강의 CRUD (목록, 상세, 생성, 수정, 삭제)
- 강의 검색 (수강 가능한 강의)
- 수강신청 (신청, 취소, 승인, 거절)
- 내 수강신청 목록

#### **✅ Step 2 웹 화면**
- 양방향 관계 (코드 레벨에서 확인)
- 편의 메서드 (코드 레벨에서 확인)
- 비관적 락 (동시성 테스트)

### **🔧 Postman/curl로만 테스트 가능한 기능**

#### **Step 2 배치 처리 API**
```bash
# 수강신청 일괄 승인/거절
POST /api/enrollments/batch
{
  "enrollmentIds": [1, 2, 3],
  "action": "APPROVE",
  "reason": "일괄 승인 처리"
}

# 강의별 수강신청 일괄 승인/거절
POST /api/enrollments/batch/course/1?action=APPROVE&reason=강의별_일괄_승인
```

#### **Step 2 통계 기능 (Service 구현됨, API 없음)**
- 강의 전체 통계
- 강사별 강의 통계
- 월별 강의 개설 통계
- 인기 강의 TOP N
- 강의 고급 검색

#### **Step 1 팝업 기능 (Service 구현됨, API 없음)**
- 강의 커리큘럼 조회
- 강의 수강생 목록 조회

---

## 🎓 강의 활용 가이드

### **수동 테스트 순서**
1. **Step 1 기능 테스트** (30분)
2. **Step 2 기능 테스트** (30분)
3. **에러 시나리오 테스트** (15분)
4. **성능 테스트** (15분)

### **학습자 가이드**
- 각 시나리오를 순서대로 실행
- 예상 결과와 실제 결과 비교
- 에러 발생 시 원인 분석
- 성능 개선 효과 체감

**총 소요 시간: 약 90분** ⏰
