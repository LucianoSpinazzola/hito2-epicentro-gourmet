# Epicentro Gourmet - Hito 2 (Spring Boot)

Trabajo Práctico Grupal 2026 - OO2 - UNLa

## Grupo 9

### Integrantes

* Luciano Nicolás Spinazzola - [LucianoSpinazzola](https://github.com/LucianoSpinazzola)
* Vazquez, Pablo - [pablo-vaz1](https://github.com/pablo-vaz1)
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
git clone https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet.git
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
https://drive.google.com/file/d/1D7XGryUYuFaSg-SwSJwtA2BEwlU4-QyE/view?usp=sharing

5. Ejecutar la aplicación:
Desde el IDE (STS) mediante Run As > Spring Boot App, o vía terminal con:
mvn spring-boot:run
La aplicación estará disponible en http://localhost:8089

### Accesibilidad

La interfaz está desarrollada con criterios de accesibilidad para personas con discapacidad visual, compatible con lectores de pantalla como NVDA, JAWS y Narrador. Los ejemplos de anuncios corresponden a NVDA; JAWS y Narrador siguen la misma lógica.

* Regiones semánticas: el lector anuncia "región principal", "navegación" y "encabezado nivel 1" al recorrer la página, y permite saltar entre regiones con teclas rápidas (por ejemplo, D para región principal en NVDA).
* Etiquetas vinculadas: al enfocar un campo, el lector anuncia "Correo Electrónico, edición requerida" o "Contraseña, edición de contraseña requerida", en lugar de un campo sin nombre.
* Campos obligatorios: el lector agrega "requerido" al anunciar el campo, gracias a required y aria-required.
* Mensajes con role="alert": al aparecer, el lector anuncia de inmediato "alerta: Credenciales inválidas o usuario inactivo" o "alerta: Ha cerrado sesión correctamente", sin que el usuario tenga que buscarlos.
* aria-describedby: al entrar al formulario con un error, el lector lee también el mensaje de error asociado.
* Fechas como tres cuadros combinados: en el alta de empleado, NVDA anuncia "Fecha de nacimiento, grupo" y cada select como "cuadro combinado", en lugar de un input date que se lee como "botón giratorio".
* Foco visible: al navegar con Tab, el lector anuncia el elemento enfocado ("botón, Cerrar sesión segura del sistema") y el foco se ve con un contorno de alto contraste.
* Modo oscuro: el botón anuncia "Cambiar tema. Tema actual: oscuro" y actualiza el anuncio al cambiar, para que el usuario sepa el estado sin depender del color.
* Emojis decorativos: se ocultan a lectores con aria-hidden, así no se anuncia "gráfico de barras" antes del título de cada tarjeta.

### Envío de correo

El envío de correo se configura por variables de entorno, para no exponer credenciales en el repositorio. Cada persona define las suyas en el Run Configuration de STS o en las variables de entorno del sistema:

SMTP_HOST = servidor SMTP del proveedor (ej. smtp.gmail.com)
SMTP_PORT = puerto SMTP (587 para TLS, 465 para SSL)
SMTP_USERNAME = dirección de correo desde la que se envía
SMTP_PASSWORD = contraseña de aplicación o token del proveedor

Ejemplos de configuración por proveedor:

| Proveedor | SMTP_HOST | SMTP_PORT | Requisito |
| --- | --- | --- | --- |
| Gmail | smtp.gmail.com | 587 | Contraseña de aplicación de 16 dígitos (requiere verificación en dos pasos) |
| Outlook / Hotmail / Live | smtp-mail.outlook.com | 587 | Contraseña de aplicación si la cuenta tiene verificación en dos pasos |
| Office 365 | smtp.office365.com | 587 | Contraseña de aplicación o token según la organización |
| Yahoo Mail | smtp.mail.yahoo.com | 465 o 587 | Contraseña de aplicación |
| iCloud Mail | smtp.mail.me.com | 587 | Contraseña de aplicación (app-specific password) |
| Zoho Mail | smtp.zoho.com | 465 o 587 | Contraseña de aplicación si tiene MFA activado |

Para cualquier otro proveedor, buscar "configuración SMTP de [proveedor]" en su documentación y usar el host y puerto indicados.

Importante para Gmail: la contraseña normal de la cuenta no funciona para SMTP. Hay que generar una contraseña de aplicación desde la configuración de seguridad de Google (requiere tener activada la verificación en dos pasos). Google la muestra con espacios o guiones, pero al cargarla en SMTP_PASSWORD hay que pegarla sin separadores, como una cadena continua de 16 caracteres.

### Diagrama de Clases
https://drive.google.com/file/d/186UyoMGrEUDujmmbJauR2BH1_pfwHJim/view?usp=sharing

### Casos de Uso Implementados

| Caso de Uso | Responsable | Pull Request |
| --- | --- | --- |
| Login seguro con roles (BCrypt, CSRF, rutas protegidas) | Luciano Spinazzola | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/6 |
| Alta de Empleado (formulario accesible, validaciones, Cajero/Cocinero) | Luciano Spinazzola | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/7 |
| Reportes de Administrador (Ranking y Mayor Canon) | Pablo Vazquez | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/9 |
| Filtro por unidad de venta y Cierre de Caja | Pablo Vazquez | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/8 |
| Entidades con herencia, repositorios, servicios e implementaciones | Pablo Vazquez | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/3 |
| Vistas, controllers y DTOs | Pablo Vazquez | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/5 |
| Configuración de properties y entidades sin herencia | Luciano Spinazzola | https://github.com/LucianoSpinazzola/hito2-epicentro-gourmet/pull/4 |

### Metodología de Trabajo

Cada caso de uso se desarrolla en una rama independiente siguiendo el formato CUFuncionalidadResponsable (por ejemplo, CUAltaEmpleadoJuanPerez) y se integra a main mediante Pull Request, incluyendo en la descripción los detalles técnicos de la solución y en este README el enlace correspondiente al PR.

