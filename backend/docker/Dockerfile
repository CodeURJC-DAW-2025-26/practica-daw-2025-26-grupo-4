# Frontend build stage
FROM node:22-alpine AS frontend-builder

WORKDIR /workspace/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
ENV REACT_ROUTER_BASENAME=/new
RUN npm run build

# Backend build stage
FROM maven:3.9-eclipse-temurin-21 AS backend-builder

WORKDIR /workspace

COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
COPY --from=frontend-builder /workspace/frontend/build/client/ backend/src/main/resources/static/new/

WORKDIR /workspace/backend
RUN mvn -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-builder /workspace/backend/target/*.jar /app/app.jar

EXPOSE 8443

CMD ["java", "-jar", "/app/app.jar"]
