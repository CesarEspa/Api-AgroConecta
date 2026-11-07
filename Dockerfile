FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar archivos Maven
COPY pom.xml .
COPY src ./src
# Compilar la aplicación
RUN mvn clean package -DskipTests
# Etapa 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar
# Puerto que Render asignará dinámicamente
EXPOSE 8080
# Ejecutar la aplicación
# Plan gratis de Render: 512 MB de RAM
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-XX:+UseSerialGC","-jar","app.jar"]