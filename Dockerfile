# Dockerfile para Railway
FROM maven:3.8.6-openjdk-11 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Usar Jetty para servir la aplicación
FROM jetty:9.4-jre11

# Copiar el WAR al directorio de webapps de Jetty
COPY --from=build /app/target/pinto.war /var/lib/jetty/webapps/ROOT.war

# Puerto por defecto de Railway
ENV PORT=8080
EXPOSE 8080

# Jetty se inicia automáticamente
