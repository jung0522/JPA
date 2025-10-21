@echo off
REM JPA 애플리케이션 배포 스크립트 (Windows)
REM 사용법: deploy.bat [student01|student02|student03] [포트]

set STUDENT_ID=%1
if "%STUDENT_ID%"=="" set STUDENT_ID=student01

set PORT=%2
if "%PORT%"=="" set PORT=8080

echo 🚀 JPA 애플리케이션 배포 시작 - %STUDENT_ID% (포트: %PORT%)

REM 1. JAR 파일 빌드
echo 📦 JAR 파일 빌드 중...
call gradlew.bat bootJar

if %errorlevel% neq 0 (
    echo ❌ JAR 빌드 실패
    exit /b 1
)

echo ✅ JAR 빌드 완료

REM 2. Docker 이미지 빌드
echo 🐳 Docker 이미지 빌드 중...
docker build -t jpa-%STUDENT_ID% .

if %errorlevel% neq 0 (
    echo ❌ Docker 이미지 빌드 실패
    exit /b 1
)

echo ✅ Docker 이미지 빌드 완료

REM 3. 기존 컨테이너 정리
echo 🧹 기존 컨테이너 정리 중...
docker stop jpa-%STUDENT_ID% >nul 2>&1
docker rm jpa-%STUDENT_ID% >nul 2>&1

REM 4. 새 컨테이너 실행
echo 🚀 새 컨테이너 실행 중...
docker run -d -p %PORT%:8080 --name jpa-%STUDENT_ID% jpa-%STUDENT_ID%

if %errorlevel% equ 0 (
    echo ✅ 배포 완료!
    echo 🌐 애플리케이션 접속: http://localhost:%PORT%
    echo 📋 컨테이너 상태 확인: docker logs -f jpa-%STUDENT_ID%
) else (
    echo ❌ 컨테이너 실행 실패
    exit /b 1
)
