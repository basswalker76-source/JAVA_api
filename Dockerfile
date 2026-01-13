# --- STAGE 1: Build the JAR ---
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
# Copy only the gradle files first (better for caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Build the application (skipping tests to save time on Render)
RUN ./gradlew build -x test --no-daemon

# --- STAGE 2: Run the JAR ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# This line finds the JAR created in the build stage and copies it here
COPY --from=build /app/build/libs/web-api-product-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
