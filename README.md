# Epicentro Gourmet - Hito 2 (Spring Boot)

Trabajo Práctico Grupal 2026 - OO2 - UNLa

## Grupo 9

### Integrantes

* Luciano Nicolás Spinazzola - [LucianoSpinazzola]
* Apellido, Nombre - [COMPLETAR USUARIO GITHUB]
* Apellido, Nombre - [COMPLETAR USUARIO GITHUB]
* Apellido, Nombre - [COMPLETAR USUARIO GITHUB]

### Descripción

Sistema de gestión para el centro de convenciones y predio ferial "Epicentro Gourmet", que administra festivales temáticos, unidades de venta (Food Truck y Puesto Desarmable), personal (cocina y cajeros), platos, pedidos y costos asociados.

### Tecnologías

* Java 17
* Spring Boot 4.1.1
* Spring Data JPA / Hibernate
* Spring Security
* Thymeleaf
* MySQL
* ModelMapper
* Maven
* Lombok

### Pasos para correr la aplicación

1. Clonar el repositorio:
git clone [COMPLETAR - URL DEL REPO]
cd hito2

2. Crear la base de datos:
Ejecutar en MySQL (Workbench o consola):
CREATE DATABASE epicentro_gourmet_grupo_9;

3. Configurar variables de entorno:
La aplicación no utiliza credenciales hardcodeadas. Cada integrante debe configurar en su entorno de ejecución (Run Configuration en el IDE):
DB_USER = [tu usuario de MySQL local]
DB_NAME = epicentro_gourmet_grupo_9
DB_PASSWORD = [tu contraseña de MySQL local]

4. Cargar datos de prueba:
[COMPLETAR - Nombre del script SQL con inserts de prueba para la defensa]

5. Ejecutar la aplicación:
Desde el IDE (STS) mediante Run As > Spring Boot App, o vía terminal con:
mvn spring-boot:run
La aplicación estará disponible en http://localhost:8080

### Diagrama de Clases

[COMPLETAR - Link al diagrama de clases una vez validado]

### Casos de Uso Implementados

| Caso de Uso | Responsable | Pull Request |
| --- | --- | --- |
| [COMPLETAR] | [COMPLETAR] | [COMPLETAR] |

### Metodología de Trabajo

Cada caso de uso se desarrolla en una rama independiente siguiendo el formato CUFuncionalidadResponsable (por ejemplo, CUAltaEmpleadoJuanPerez) y se integra a main mediante Pull Request, incluyendo en la descripción los detalles técnicos de la solución y en este README el enlace correspondiente al PR.
