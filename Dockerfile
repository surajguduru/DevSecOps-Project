# 1. Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom first for caching dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests

# 2. Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/image-editor-1.0-SNAPSHOT.jar app.jar

# Expose the port your app uses
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
