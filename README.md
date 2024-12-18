# AiPower

[![Aipower Docker Image CI](https://github.com/nguemechieu/aipower/actions/workflows/docker-image.yml/badge.svg)](https://github.com/nguemechieu/aipower/actions/workflows/docker-image.yml)

![aipower](./aipower.ico)

Welcome to **AiPower**, a powerful application designed to revolutionize trading and analysis using modern technologies. This project combines a Spring Boot backend and a React frontend to deliver an efficient and intuitive user experience. With Docker support, deployment has never been easier!

---

## Features

- **Powerful Backend**: Built with Spring Boot, providing robust and secure API endpoints for core functionalities.
- **Modern Frontend**: Developed with React, ensuring a dynamic and user-friendly interface.
- **Dockerized Deployment**: Use Docker to containerize and deploy the application effortlessly.
- **Automated Environment Setup**: The project automatically checks for required dependencies (Java and Node.js) and installs them if necessary.
- **Cross-Platform Compatibility**: Works on Windows, macOS, and Linux.

---

## Prerequisites

Before starting, ensure you have the following installed:

- **Docker** and **Docker Compose**
- **Java** (JDK 11 or later) *(Optional, required for local development)*
- **Node.js** (16.x or later) *(Optional, required for local development)*

---

## Installation and Setup

### 1. Clone the Repository
```bash
git clone https://github.com/nguemechieu/aipower.git
cd aipower
```

### 2. Using Docker
To run the application with Docker:

1. Build and start the containers:
   ```bash
   docker-compose up --build
   ```

2. Access the application:
    - **Frontend**: [http://localhost:3000](http://localhost:3000)
    - **Backend**: [http://localhost:8080](http://localhost:8080)

### 3. Local Development
For development without Docker, follow these steps:

#### Install Dependencies
Dependencies for both the backend and frontend will be handled automatically. The provided script ensures:

- **Java** is installed via `choco` on Windows or prompts manual installation.
- **Node.js** is installed via `choco` on Windows or prompts manual installation.

#### Run the Application Locally
Run the startup script:
```bash
node start.js
```

The script will:

1. Check for Java and Node.js installations.
2. Install missing dependencies (if necessary).
3. Build the frontend React app (for production mode) or start it (for development mode).
4. Start the Spring Boot backend.

---

## Docker Configuration

### Dockerfile (Backend)
Located in the `backend/` directory:

```dockerfile
# Use a lightweight Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy backend build files
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
```

### Dockerfile (Frontend)
Located in the `frontend/` directory:

```dockerfile
# Use Node.js image for React build
FROM node:16 AS build

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the app files
COPY . .

# Build the React app
RUN npm run build

# Use Nginx to serve the React app
FROM nginx:stable
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### `docker-compose.yml`
Located in the root directory:

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    networks:
      - aipower-network
    depends_on:
      - frontend

  frontend:
    build:
      context: frontends
      dockerfile: Dockerfile
    ports:
      - "3000:80"
    networks:
      - aipower-network

networks:
  aipower-network:
```

---

## Development Mode

To run the project in development mode:

1. Start the **frontend**:
   ```bash
   cd frontend
   npm start
   ```

2. Start the **backend**:
   ```bash
   ./gradlew bootRun
   ```

---

## Production Mode

To build and run the application for production:

1. Build the frontend:
   ```bash
   cd frontend
   npm run build
   ```

2. Start the backend in production mode:
   ```bash
   ./gradlew bootRun -Pprod
   ```

---

## Directory Structure

```
aipower/
├── backend/           # Spring Boot backend code
│   ├── Dockerfile     # Docker configuration for the backend
├── frontend/          # React frontend code
│   ├── Dockerfile     # Docker configuration for the frontend
├── docker-compose.yml # Docker Compose configuration
├── start.js           # Automated setup and start script
├── README.md          # Project documentation
└── ...
```

---

## Scripts

### Start the Application with Docker
```bash
docker-compose up --build
```

### Start the Application Locally
```bash
node start.js
```

### Backend Only
```bash
./gradlew bootRun
```

### Frontend Only
```bash
cd frontend
npm start
```

### Build Frontend for Production
```bash
cd frontend
npm run build
```

---

## Troubleshooting

### Missing Dependencies
If you encounter missing dependencies, you can manually install them:

- **Docker**:
    - [Docker Installation Guide](https://docs.docker.com/get-docker/)

- **Java**:
    - [Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
    - [AdoptOpenJDK](https://adoptopenjdk.net/)
    - Use `brew install openjdk` on macOS or `sudo apt install openjdk` on Linux.

- **Node.js**:
    - [Node.js Official Website](https://nodejs.org/)

### Port Conflicts
Ensure no other applications are using the default ports:
- **Backend**: `8080`
- **Frontend**: `3000`

---

## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m "Description of changes"`.
4. Push to your fork: `git push origin feature-name`.
5. Open a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Support

For issues or feature requests, please open an issue in the repository or contact us at `nguemechieu@live.com`.

---
