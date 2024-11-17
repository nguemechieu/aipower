# Use Node.js to build the frontend
FROM node:latest AS frontend-builder
WORKDIR /app

# Copy only the necessary files for dependency installation
COPY package*.json ./
RUN npm install --production

# Copy the rest of the frontend code and build
COPY . .
RUN npm run build

# Stage 2: Serve the frontend
FROM nginx:latest
WORKDIR /usr/share/nginx/html
RUN apt-get update && apt-get install -y libtcnative-1

# Copy the built frontend files to the Nginx static directory
COPY --from=frontend-builder /app/build .

# Expose the frontend server port
EXPOSE 80

# Run Nginx
CMD ["nginx", "-g", "daemon off;"]
