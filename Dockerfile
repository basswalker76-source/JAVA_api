# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy everything
COPY . .

# Fix permissions
RUN chmod +x gradlew

# Run the build
# Added -Porg.gradle.java.installations.auto-download=false to prevent toolchain errors
RUN ./gradlew clean bootJar -x test -Porg.gradle.java.installations.auto-download=false

# Stage 2: Run
# Match the Java version here too (21 or 17)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
