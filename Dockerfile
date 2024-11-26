# Base stage: Use Node.js image for building the application
FROM node:latest AS builder

# Set working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (if available) to leverage Docker caching
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install  --yes
# Copy the rest of the application files
COPY . .
# Build the application for development
RUN npm run development
# Define the default command to run the application
CMD ["npm", "development"]
