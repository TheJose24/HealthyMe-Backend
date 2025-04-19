# HealthyMe - Sistema para Clínicas Médicas

## Descripción General
**HealthyMe** es un sistema de microservicios desarrollado en Java con Spring Boot para la gestión integral de clínicas médicas. Esta arquitectura facilita la administración escalable de pacientes, personal médico, citas médicas, consultas, laboratorios y toda la infraestructura asociada a un entorno clínico moderno.

Este proyecto se desarrolla como parte del curso "Desarrollo Web Integrado" de la Universidad Tecnológica del Perú, constituyendo una solución empresarial completa para el sector salud.

## Arquitectura de Microservicios

El sistema está dividido en los siguientes microservicios:

1. **Servicio de Autenticación y Autorización**
   - Gestión de usuarios, roles y permisos
   - Autenticación y control de acceso

2. **Servicio de Personal Médico**
   - Administración de médicos, enfermeros, técnicos y administradores
   - Gestión de especialidades y horarios

3. **Servicio de Pacientes**
   - Información de pacientes y seguros
   - Historial médico y triajes

4. **Servicio de Citas**
   - Programación y seguimiento de citas médicas
   - Estado y gestión de agendas

5. **Servicio de Consultas/Diagnósticos**
   - Registro de consultas médicas
   - Emisión y control de recetas

6. **Servicio de Laboratorio**
   - Reservas de laboratorio
   - Gestión de exámenes y resultados

7. **Servicio de Infraestructura**
   - Administración de sedes y consultorios
   - Gestión de laboratorios y espacios

8. **Servicio de Pagos**
   - Procesamiento de transacciones
   - Facturación y control financiero

9. **Servicio de Notificaciones**
   - Envío de avisos por múltiples canales
   - Gestión de plantillas y adjuntos

## Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación principal
- **Spring Boot 3.4.4**: Framework base para el desarrollo
- **Spring Data JPA**: Persistencia y acceso a datos
- **Spring Security**: Autenticación y autorización
- **Java Mail**: Envío de correos electrónicos
- **MySQL 8.0**: Base de datos relacional para cada microservicio
- **Flyway**: Gestión de migraciones de base de datos
- **MapStruct 1.6.3**: Mapeo entre entidades y DTOs
- **Lombok 1.18.38**: Reducción de código boilerplate
- **SpringDoc 2.8.6**: Documentación de la API con OpenAPI
- **Kafka**: Sistema de mensajería para comunicación entre servicios
- **Resilience4j**: Implementación de patrones de resiliencia
- **Actuator**: Monitoreo y gestión de aplicaciones
- **Maven**: Gestión de dependencias y construcción del proyecto

## Patrones Implementados

### Patrón Saga
Implementado para transacciones distribuidas entre microservicios, manteniendo la consistencia de datos en operaciones que abarcan múltiples servicios.

**Ejemplo**: Creación de citas médicas
```
1. Cliente solicita nueva cita → Servicio de Citas
2. Verificar paciente válido → Servicio de Pacientes
3. Verificar disponibilidad médico → Servicio de Personal
4. Verificar consultorio disponible → Servicio de Infraestructura
5. Confirmación de cita → Cliente
```

En caso de error, se ejecutan acciones compensatorias para mantener la consistencia.

### Circuit Breaker
Utilizado para evitar llamadas a servicios no disponibles y proporcionar respuestas alternativas.

**Implementado en**:
- Servicio de citas: Para llamadas a servicios de pacientes, personal médico, etc.
- Servicio de pagos: Para comunicación con pasarelas de pago externas
- Servicio de notificaciones: Para envío de mensajes por diferentes canales

## Comunicación entre Servicios

### Kafka
Implementado para comunicación asíncrona entre servicios:

**Tópicos principales**:
- `citas-events`: Eventos de creación y modificación de citas
- `consultas-events`: Notificaciones sobre consultas médicas
- `examenes-events`: Información sobre exámenes de laboratorio
- `pagos-events`: Transacciones y estados de pago
- `notifications-commands`: Comandos para envío de notificaciones

## Requisitos Previos
Para configurar y ejecutar este proyecto, necesitas:

- **JDK 21** o superior
- **Maven 3.8+**
- **MySQL 8.0** o superior
- **Kafka**
- **Git**
- **IDE** recomendado: IntelliJ IDEA

## Estructura del Proyecto

```
healthyme-app/
├── security-service/         # Servicio de autenticación
├── healthyme-personal/       # Servicio de personal médico
├── healthyme-pacientes/      # Servicio de pacientes
├── healthyme-citas/          # Servicio de citas
├── healthyme-consultas/      # Servicio de consultas/diagnósticos
├── healthyme-laboratorio/    # Servicio de laboratorio
├── healthyme-infraestructura/# Servicio de infraestructura
├── healthyme-payment/        # Servicio de pagos
├── healthyme-notification/   # Servicio de notificaciones
├── config-server/            # Configuración centralizada
├── api-gateway/              # Puerta de enlace API
└── discovery-server/         # Servidor de descubrimiento
```

## Configuración del Proyecto

### 1. Clonar el Repositorio
```bash
git clone https://github.com/TheJose24/HealthyMe-Backend.git
cd HealthyMe-Backend
```

### 2. Configuración de Variables de Entorno
Cada microservicio utiliza un archivo .env para gestionar las variables de entorno en desarrollo local. Crea un archivo .env en la raíz de cada microservicio con el siguiente contenido:

```properties
# Configuración de Base de Datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=healthyme_{servicio}_db
DB_USER=tu_usuario
DB_PASSWORD=tu_contraseña

# Configuración del Servidor
PORT=808x  # Diferente para cada microservicio

# Configuración de Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 3. Configuración de Perfiles
Cada microservicio está configurado con múltiples perfiles:

- **dev**: Entorno de desarrollo local
- **prod**: Entorno de producción

Para especificar el perfil en tu IDE:

1. Busca la opción `Edit Configurations` dentro de la pestaña `Run`

2. En active profiles indica el nombre del perfil con el que se va a ejecutar la aplicación (dev o prod)

## Flujo de Trabajo con Gitflow

### Estructura de Ramas
- **`master`**: Código en producción estable
- **`develop`**: Rama de integración principal
- **`feature/*`**: Ramas para nuevas funcionalidades

### Convención para Nombrar Ramas
```
feature/[microservicio]-[descripción-breve]
```

Ejemplos:
- `feature/auth-login-pacientes`
- `feature/citas-validacion-horarios`
- `feature/recetas-envio-email`

### Formato de Commits
Todos los commits deben seguir esta estructura:
```
tipo(microservicio): Título descriptivo

- Cuerpo del commit con detalles del cambio.
Issue #número
```

**Tipos de Commits**:
- `feat`: Nuevas funcionalidades
- `fix`: Corrección de errores
- `docs`: Cambios en documentación
- `style`: Cambios que no afectan el funcionamiento
- `refactor`: Refactorización de código existente
- `test`: Adición o modificación de pruebas
- `chore`: Tareas de mantenimiento, configuraciones, etc.

## Documentación de la API
Después de iniciar cada microservicio, su documentación estará disponible en:
```
http://localhost:808x/healthyme-docs
```

## Contribuir al Proyecto
Para contribuir al desarrollo:

1. Crea una rama a partir de `develop`
2. Implementa los cambios siguiendo las convenciones de código
3. Realiza commits descriptivos según el formato establecido
4. Crea un Pull Request para su revisión
