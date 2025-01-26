# Stage 1: Build
FROM gradle:8.12.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Stage 2: Run
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/url-shortener-java-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
