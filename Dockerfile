FROM eclipse-temurin:17-jre AS base

FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM base
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

CMD ["java", "-Xms512m", "-Xmx750m", "-jar", "app.jar"]