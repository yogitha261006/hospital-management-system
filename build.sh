#!/bin/bash
# Build script for Render deployment
# This builds the React frontend and copies it into Spring Boot's static resources,
# then builds the Spring Boot JAR.

set -e

echo "=== Step 1: Building React Frontend ==="
cd frontend
npm install
VITE_API_URL="" npm run build
cd ..

echo "=== Step 2: Copying frontend build to Spring Boot static resources ==="
rm -rf backend/src/main/resources/static
mkdir -p backend/src/main/resources/static
cp -r frontend/dist/* backend/src/main/resources/static/

echo "=== Step 3: Building Spring Boot JAR ==="
cd backend
./mvnw clean package -DskipTests || mvn clean package -DskipTests
cd ..

echo "=== Build Complete! ==="
echo "JAR file is at: backend/target/management-0.0.1-SNAPSHOT.jar"
