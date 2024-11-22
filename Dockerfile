
FROM ubuntu:latest

WORKDIR /app
# Install required tools
RUN apt-get update
RUN apt-get install -y curl

# Install required dependencies
RUN  apt-get install -y findutils



WORKDIR /app/frontend
# Stage 1: Build the frontend
FROM node:latest AS frontend-builder

# Install dependencies and build the React app
COPY package*.json  package*.json
RUN npm install
RUN npm run build

COPY frontend/ ./frontend

# Stage 2: Build the backend

FROM openjdk:17-alpine AS backend-runner
WORKDIR /app

# Copy the backend files and frontend build output
COPY ./  ./


COPY --from=frontend-builder /app/frontend/build ./src/main/resources/static

# Ensure Gradle wrapper is executable and build the backend
WORKDIR /app
RUN chmod +x gradlew && ./gradlew build

# Expose the backend port
EXPOSE 8080

# Run the backend
CMD ["java", "-jar", "build/libs/aipower-0.0.1-SNAPSHOT.jar"]