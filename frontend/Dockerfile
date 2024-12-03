# Stage 1: Build React App
FROM node:latest AS frontend-builder
WORKDIR /aipower/frontend

# Copy frontend package files and install dependencies
COPY frontend/package*.json ./
RUN npm install --production

# Copy the rest of the frontend source code
COPY frontend/ ./

# Build the frontend for production
RUN npm run build
