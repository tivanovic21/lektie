# Build context: services/  (docker compose --profile api up -d --build)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Gradle files first: this layer (and the downloaded dependencies) is cached until they change.
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle gradle
COPY common/build.gradle.kts common/
COPY mobile-api/build.gradle.kts mobile-api/
COPY web-api/build.gradle.kts web-api/
# Windows checkouts (core.autocrlf=true) give gradlew CRLF line endings, which sh cannot run.
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew :web-api:dependencies --no-daemon -q > /dev/null

COPY common common
COPY web-api web-api
RUN ./gradlew :web-api:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 app
COPY --from=build /src/web-api/build/libs/*.jar app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
