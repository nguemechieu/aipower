# Base stage: Install required tools
FROM ubuntu:latest AS base
WORKDIR /app
RUN apt-get update && apt-get install -y curl findutils && rm -rf /var/lib/apt/lists/*
RUN  apt-get install -y xargs

# Stage 1: Build the frontend
FROM node:latest AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json  ./
RUN npm install
COPY ./frontend  ./
RUN npm run build

# Stage 2: Build the backend
FROM openjdk:23  AS backend-builder
WORKDIR /app
COPY  ./ ./app/.
COPY --from=frontend-builder /app/frontend/build ./src/main/resources/static
RUN chmod +x gradlew && ./gradlew build

# Stage 3: Production runner
FROM openjdk:23 AS production
WORKDIR /app
COPY --from=backend-builder /app/build/libs/aipower.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
