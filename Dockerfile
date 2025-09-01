## Use official Java 21 image
#FROM openjdk:21-jdk-slim
#
## Set working directory inside the container
#WORKDIR /app
#
## Copy Gradle wrapper and build files
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#
## Copy source code
#COPY src src
#
## Make gradlew executable
#RUN chmod +x gradlew
#
## Build the project (skip tests)
#RUN ./gradlew build -x test
#
## Expose port 8080
#EXPOSE 8080
#
## Run the Spring Boot JAR
#CMD ["java", "-jar", "build/libs/app.jar"]

##---- Stage 1: Build ----
#FROM openjdk:21-jdk-slim AS build
#
#WORKDIR /app
#
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#
#RUN chmod +x gradlew
#RUN ./gradlew dependencies || true
#
#COPY src src
#RUN ./gradlew build -x test
#
## ---- Stage 2: Runtime ----
#FROM openjdk:21-jdk-slim
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
#EXPOSE 8080
#CMD ["java", "-jar", "app.jar"]



# Use official OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle/Maven build files and source
COPY build/libs/app.jar app.jar

# Install CA certificates (required for MongoDB Atlas SSL)
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*

# Expose port 8080
EXPOSE 8080

# Set environment variable for MongoDB URI (can be overridden by Render)
ENV MONGO_URI="mongodb+srv://kropta95_db_user:KkOFF9RrMvuHUWBa@feedsystemcluster.3djonio.mongodb.net/FeedSystemDB?retryWrites=true&w=majority"

# JVM options for better performance
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod"

# Run the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
