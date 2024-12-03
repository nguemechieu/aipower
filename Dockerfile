# Stage 1: Build React App
FROM node:latest AS frontend-builder
WORKDIR /aipower/frontend

# Copy frontend package files and install dependencies
COPY frontend/package*.json ./
RUN npm ci --production

# Copy the rest of the frontend source code
COPY frontend/ ./

# Build the frontend for production
RUN npm run build



# Stage 2: Build Spring Boot Backend
FROM gradle:8.0-jdk-jammy AS backend-builder

# Ensure correct Java path and set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-23-openjdk
RUN mkdir -p $JAVA_HOME && ln -s $(dirname $(dirname $(readlink -f $(which java)))) $JAVA_HOME

WORKDIR /aipower

# Copy Gradle wrapper and build files
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# Ensure gradlew is executable
RUN chmod +x gradlew

# Cache dependencies
RUN ./gradlew dependencies --no-daemon

# Copy backend source code and build the application
COPY . ./
RUN ./gradlew bootJar --no-daemon

# Stage 3: Final Deployment Image
FROM openjdk:22 AS production

WORKDIR /aipower

# Copy built backend JAR
COPY --from=backend-builder /aipower/build/libs/*.jar aipower.jar

# Copy built frontend files
COPY --from=frontend-builder /aipower/frontend/build ./src/main/resources/static

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Expose application port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "aipower.jar"]
