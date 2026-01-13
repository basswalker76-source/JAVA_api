# --- STAGE 1: Build the JAR ---
# Use JDK 21 for the build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy gradle executable and configuration
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Grant execution permission
RUN chmod +x gradlew

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew build -x test --no-daemon

# --- STAGE 2: Run the JAR ---
# Use JDK 21 for the runtime stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/build/libs/web-api-product-0.0.1-SNAPSHOT.jar.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
