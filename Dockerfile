# Stage 1: Build Frontend
FROM node:20 AS frontend-builder
WORKDIR /frontend

# Copy package.json and package-lock.json and install dependencies
COPY frontend/package*.json ./
RUN npm ci

# Copy the rest of the frontend source code and build
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot Backend
FROM gradle:8.3-jdk-jammy AS backend-builder

# Install Java 23
RUN apt-get update && \
    apt-get install -y curl && \
    mkdir -p /usr/lib/jvm && \
    curl -L -o temurin.tar.gz https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.1%2B11/OpenJDK23U-jdk_x64_linux_hotspot_23.0.1_11.tar.gz && \
    tar -xzf temurin.tar.gz -C /usr/lib/jvm && \
    mv /usr/lib/jvm/jdk-23.0.1+11 /usr/lib/jvm/jdk-23.0.1 && \
    rm temurin.tar.gz

# Set JAVA_HOME and update PATH
ENV JAVA_HOME=/usr/lib/jvm/jdk-23.0.1
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /backend

# Copy Gradle wrapper and build files
COPY backend/gradlew backend/gradlew.bat backend/settings.gradle backend/build.gradle ./
COPY backend/gradle ./gradle

# Ensure gradlew is executable
RUN chmod +x gradlew

# Cache dependencies
RUN ./gradlew dependencies --no-daemon

# Copy backend source code and build the application
COPY backend/ ./
RUN ./gradlew bootJar --no-daemon

# Stage 3: Production
FROM openjdk:23 AS production

WORKDIR /backend

# Copy built backend JAR
COPY --from=backend-builder /backend/build/libs/*.jar aipower.jar

# Copy frontend build output
COPY --from=frontend-builder /frontend/build ./src/main/resources/static

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Expose the application port
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "aipower.jar"]
