# --- STAGE 1: Build the JAR ---
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Copy gradle executable and configuration first
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# FIX: Grant execution permission to the gradlew script
RUN chmod +x gradlew

# Copy the source code
COPY src src

# Build the application (skipping tests for speed)
RUN ./gradlew build -x test --no-daemon

# --- STAGE 2: Run the JAR ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the jar from the build stage to the final image
COPY --from=build /app/build/libs/web-api-product-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
