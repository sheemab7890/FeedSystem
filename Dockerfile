# ---- Stage 1: Build ----
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Pre-download dependencies for caching
RUN ./gradlew clean build -x test --no-daemon || true

# Copy source code
COPY src src

# Build the project
RUN ./gradlew build -x test --no-daemon

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy fat jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]



