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
FROM openjdk:23 AS backend-builder

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

WORKDIR /aipower

# Copy built backend JAR
COPY --from=backend-builder /backend/build/libs/*.jar aipower.jar

# Copy frontend build output
COPY --from=frontend-builder /frontend/build ./backend/src/main/resources/static

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Expose the application port
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "aipower.jar"]
