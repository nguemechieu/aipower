
FROM ubuntu:latest
# Install required tools
RUN apt-get update
RUN apt-get install -y curl

# Install required dependencies
RUN  apt-get install -y findutils

WORKDIR /app/frontend


# Stage 1: Build the frontend
FROM node:20 AS frontend-builder

# Install dependencies and build the React app
COPY package*.json ./package*.json
RUN npm install
RUN npm run build

COPY frontend/ ./

# Stage 2: Build the backend

FROM openjdk:23 AS backend-runner
WORKDIR /app

# Copy the backend files and frontend build output
COPY ./ ./backend/


COPY --from=frontend-builder /app/frontend/build ./backend/src/main/resources/static

# Ensure Gradle wrapper is executable and build the backend
WORKDIR /app/backend
RUN chmod +x gradlew && ./gradlew build

# Expose the backend port
EXPOSE 8080

# Run the backend
CMD ["java", "-jar", "build/libs/backend-0.0.1-SNAPSHOT.jar"]
