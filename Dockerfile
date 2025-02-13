FROM eclipse-temurin:17-jre AS base

FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM base
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]