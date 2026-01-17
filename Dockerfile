# Stage 1: Build JAR using Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/image-editor-1.0-SNAPSHOT.jar app.jar

# Expose port for CI container test
EXPOSE 8080

# Start the HealthServer for CI test
CMD ["java", "-cp", "app.jar:.", "com.example.HealthServer"]
