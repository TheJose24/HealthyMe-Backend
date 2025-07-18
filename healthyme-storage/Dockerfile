FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR construido
COPY target/healthyme-storage-*.jar app.jar

# Crear directorio para archivos
RUN mkdir -p /app/storage/files

# Exponer puerto
EXPOSE 8088

# Configurar variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8088/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]