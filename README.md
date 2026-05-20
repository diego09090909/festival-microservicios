MS-Lineup
Integrantes del equipo
Diego Jimenez

Kevin Gonzales

Jose Sanchez

Descripción
Microservicio encargado de la gestión de la programación musical del festival: administración de artistas y la asignación horaria en los escenarios (lineup). Utiliza comunicación síncrona mediante Feign Client para conectarse con MS-Eventos, validando la existencia y estado de las fechas de los festivales antes de programar bloques musicales.

Tecnologías
Spring Boot 3.4.5

MySQL

OpenFeign (Feign Client)

Spring Security & JWT

Spring Data JPA & Hibernate

Lombok

Puerto
MS-Lineup: 8804

Endpoints
Gestión de Artistas (Artistas)
POST /api/v1/artistas - Registrar un nuevo artista o banda.

GET /api/v1/artistas/{id} - Obtener el detalle de un artista por ID.

PUT /api/v1/artistas/{id} - Actualizar datos de un artista (nombre, descripción, género, etc.).

DELETE /api/v1/artistas/{id} - Eliminar un artista del sistema.

Gestión de Programación (ProgramacionController)
POST /api/v1/programacion - Crear un nuevo bloque horario/bloque de artista en el lineup (valida con MS-Eventos a través de EventoClient).

GET /api/v1/programacion/evento/{eventoId} - Obtener todo el lineup o grilla musical completa de un evento específico.

GET /api/v1/programacion/escenario/{nombreEscenario} - Consultar las presentaciones agendadas por escenario.

DELETE /api/v1/programacion/{id} - Cancelar o remover un bloque de artista del lineup.

Requisitos
Java 17 (Eclipse Adoptium OpenJDK o similar)

Base de datos MySQL activa en ambiente local o remoto

MS-Eventos corriendo en el puerto 8802 (requerido para la inyección y consumo de EventoClient)

Pasos para ejecutar
Asegúrate de tener levantada la base de datos MySQL correspondiente para el proyecto del festival.

Asegúrate de que el microservicio MS-Eventos esté activo y escuchando peticiones en su puerto asignado (8802).

Abre una terminal en la raíz de la carpeta del proyecto (ms-lineup1.1).

Ejecuta el comando de construcción y arranque de Maven:

Bash
./mvnw spring-boot:run

