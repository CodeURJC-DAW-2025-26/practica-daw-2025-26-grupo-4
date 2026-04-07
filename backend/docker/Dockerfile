# Imagen base para el contenedor de compilación
FROM maven:3.9-eclipse-temurin-21 AS builder

# Directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app

# Copiamos primero el pom.xml para cachear dependencias
COPY pom.xml .

# Descargamos dependencias
RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos el proyecto
RUN mvn -B package -DskipTests

# Usa Eclipse Temurin 21 JRE
FROM eclipse-temurin:21-jre

# Directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app

# Copiamos el keystore para SSL
COPY src/main/resources/keystore.jks /usr/src/app/keystore.jks

# Copiamos el JAR compilado desde la fase builder
COPY --from=builder /usr/src/app/target/*.jar /usr/src/app/app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8443

# Comando por defecto al iniciar el contenedor
CMD ["java", "-jar", "app.jar"]
