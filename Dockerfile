<<<<<<< HEAD
# --- STAGE 1: Build Stage ---
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 1. Copy the wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. FIX: Give execution permission to the Gradle wrapper
RUN chmod +x gradlew

# 3. Copy the source code
COPY src src

# 4. Build the JAR file (ignoring tests to speed up Render builds)
RUN ./gradlew build -x test --no-daemon

# --- STAGE 2: Run Stage ---
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# 5. FIX: Use a wildcard (*.jar) to safely pick up the built jar 
# and avoid "file not found" errors due to long filenames
COPY --from=build /app/build/libs/web-api-product-0.0.1-SNAPSHOT.jar app.jar

# 6. Expose the port Spring Boot uses
EXPOSE 8080

# 7. Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
=======
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
>>>>>>> 93951d1e10d6df12060c2d892b59c4c515b498ef
