# JPA 애플리케이션 배포 가이드

이 프로젝트는 JPA 애플리케이션을 JAR 파일부터 Docker 컨테이너까지 단계별로 배포하는 실습을 위한 파일들을 포함합니다.

## 📁 생성된 파일들

- `Dockerfile` - Docker 이미지 빌드용 파일
- `docker-compose.yml` - Docker Compose 설정 파일
- `nginx.conf` - Nginx 리버스 프록시 설정 파일
- `deploy.sh` - Linux/Mac 배포 스크립트
- `deploy.bat` - Windows 배포 스크립트

## 🚀 배포 방법

### 1. 순수 JAR 배포

```bash
# JAR 파일 빌드
./gradlew bootJar

# 서버에 업로드 후 실행
java -jar build/libs/jpa-0.0.1-SNAPSHOT.jar --server.port=8080

# 백그라운드 실행 (터미널 종료 이슈 해결)
nohup java -jar build/libs/jpa-0.0.1-SNAPSHOT.jar --server.port=8080 > app.log 2>&1 &
```

### 2. Docker 배포

```bash
# Docker 이미지 빌드
docker build -t jpa-app .

# 컨테이너 실행
docker run -d -p 8080:8080 --name jpa-app jpa-app

# 로그 확인
docker logs -f jpa-app

# 컨테이너 중지/삭제
docker stop jpa-app && docker rm jpa-app
```

### 3. Docker Compose 배포

```bash
# Compose로 실행
docker compose up -d

# 종료
docker compose down
```

### 4. 자동화된 배포 스크립트 사용

#### Linux/Mac
```bash
# 기본 실행 (student01, 포트 8080)
./deploy.sh

# 특정 학생 ID와 포트로 실행
./deploy.sh student02 8081
```

#### Windows
```cmd
REM 기본 실행 (student01, 포트 8080)
deploy.bat

REM 특정 학생 ID와 포트로 실행
deploy.bat student02 8081
```

## 🌐 Nginx 리버스 프록시 설정

여러 학생의 애플리케이션을 하나의 도메인으로 접근할 수 있도록 설정:

- `http://localhost/student01/` → `http://localhost:8080/`
- `http://localhost/student02/` → `http://localhost:8081/`
- `http://localhost/student03/` → `http://localhost:8082/`

## 📋 체크리스트

- [ ] JAR 포그라운드 종료 이슈 관찰
- [ ] nohup 백그라운드 실행 성공
- [ ] Docker 이미지 빌드/실행
- [ ] Docker Compose로 실행
- [ ] Nginx 프록시 경로 접근

## 🔧 사전 요구사항

- JDK 17
- Gradle
- Docker
- (선택사항) Nginx

## 📚 학습 포인트

1. **수동 배포의 불편함**: 터미널 종료 시 프로세스 종료 이슈
2. **컨테이너 표준화의 이점**: 환경 일관성, 이식성
3. **서비스 조합의 가치**: Docker Compose를 통한 오케스트레이션
4. **리버스 프록시**: 여러 서비스를 하나의 도메인으로 통합
