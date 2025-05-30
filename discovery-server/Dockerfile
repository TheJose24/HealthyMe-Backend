FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR construido
COPY target/discovery-server-*.jar app.jar

# Exponer puerto
EXPOSE 8761

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]