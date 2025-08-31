# Use official Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Build the project (skip tests)
RUN ./gradlew build -x test

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot JAR
CMD ["java", "-jar", "build/libs/feed-system-0.0.1-SNAPSHOT.jar"]
