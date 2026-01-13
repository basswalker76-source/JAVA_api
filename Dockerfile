# Use a lightweight OpenJDK 17 image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy the built jar from Gradle build folder
COPY build/libs/web-api-product-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default: 8080)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
