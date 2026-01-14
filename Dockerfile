# --- STAGE 1: Build Stage ---
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 1. Copy only files needed to download dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. Pre-download dependencies (this layer is cached)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 3. Now copy source and build
COPY src src
RUN ./gradlew build -x test --no-daemon

# --- STAGE 2: Run Stage ---
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# 4. Copy the jar (using wildcard to be safe)
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar

# 5. Render uses the PORT env var
EXPOSE 8080

# 6. Use shell form to allow environment variable expansion
ENTRYPOINT java -jar app.jar --server.port=${PORT:-8080}