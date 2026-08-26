# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# 의존성 레이어 캐싱: 래퍼/빌드스크립트 먼저 복사
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# 소스 복사 후 빌드 (테스트는 CI에서 별도 수행하므로 여긴 제외)
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# 비루트 사용자로 실행
RUN groupadd --system app && useradd --system --gid app --home /app app

COPY --from=build /workspace/build/libs/*.jar app.jar

# 도시 이미지 저장 루트 / JWT 공개키 마운트 지점.
# k8s 에서 각각 PVC · Secret 으로 덮어씌우지만, 마운트 없이 떠도 죽지 않도록 미리 만들어 둔다.
RUN mkdir -p /var/lib/traveldiary/city-images /etc/traveldiary/keys \
 && chown -R app:app /app /var/lib/traveldiary /etc/traveldiary
USER app

EXPOSE 8081

# 컨테이너 메모리 한도(K8s limits)에 맞춰 힙 자동 조정
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
