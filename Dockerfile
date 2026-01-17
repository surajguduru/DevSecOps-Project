# 1. Build stage
FROM gradle:8.4-jdk17 AS build

WORKDIR /app

# Copy everything into the container
COPY . .

# Build the application (skip tests, already done in CI)
RUN gradle clean build -x test


# 2. Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
