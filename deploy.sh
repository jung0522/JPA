#!/bin/bash

# JPA 애플리케이션 배포 스크립트
# 사용법: ./deploy.sh [student01|student02|student03]

STUDENT_ID=${1:-student01}
PORT=${2:-8080}

echo "🚀 JPA 애플리케이션 배포 시작 - $STUDENT_ID (포트: $PORT)"

# 1. JAR 파일 빌드
echo "📦 JAR 파일 빌드 중..."
./gradlew bootJar

if [ $? -ne 0 ]; then
    echo "❌ JAR 빌드 실패"
    exit 1
fi

echo "✅ JAR 빌드 완료"

# 2. Docker 이미지 빌드
echo "🐳 Docker 이미지 빌드 중..."
docker build -t jpa-$STUDENT_ID .

if [ $? -ne 0 ]; then
    echo "❌ Docker 이미지 빌드 실패"
    exit 1
fi

echo "✅ Docker 이미지 빌드 완료"

# 3. 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker stop jpa-$STUDENT_ID 2>/dev/null || true
docker rm jpa-$STUDENT_ID 2>/dev/null || true

# 4. 새 컨테이너 실행
echo "🚀 새 컨테이너 실행 중..."
docker run -d -p $PORT:8080 --name jpa-$STUDENT_ID jpa-$STUDENT_ID

if [ $? -eq 0 ]; then
    echo "✅ 배포 완료!"
    echo "🌐 애플리케이션 접속: http://localhost:$PORT"
    echo "📋 컨테이너 상태 확인: docker logs -f jpa-$STUDENT_ID"
else
    echo "❌ 컨테이너 실행 실패"
    exit 1
fi
