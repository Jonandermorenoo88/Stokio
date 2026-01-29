# Stockio - Sistema de Gestión de Inventario

**Stockio** es una aplicación web de gestión de inventarios y almacenes desarrollada como Proyecto Final de DAW. Permite administrar múltiples almacenes, controlar el stock de productos, gestionar usuarios con diferentes roles y generar reportes.

## 🚀 Características Principales

*   **Gestión de Almacenes:** Crear, editar y eliminar almacenes.
*   **Control de Inventario:** Visualizar stock en tiempo real, valoración de inventario y movimientos.
*   **Gestión de Productos y Categorías:** Organización jerárquica de artículos.
*   **Entradas y Salidas:** Registro de movimientos mediante albaranes.
*   **Seguridad:** Autenticación de usuarios y control de acceso basado en roles (Admin, Jefe de Almacén, etc.).
*   **Exportación de Datos:** Capacidad de exportar reportes a Excel (Apache POI).

## 🛠️ Tecnologías

Este proyecto está construido con un stack tecnológico moderno basado en el ecosistema Spring:

*   **Lenguaje:** Java 21
*   **Framework Principal:** Spring Boot 3.5.7
*   **Motor de Plantillas:** Thymeleaf
*   **Base de Datos:** MySQL
*   **Persistencia:** Spring Data JPA / Hibernate
*   **Seguridad:** Spring Security
*   **Herramientas de Construcción:** Maven

## 📋 Requisitos Previos

Asegúrate de tener instalado lo siguiente antes de ejecutar el proyecto:

*   **Java JDK 21** o superior.
*   **MySQL Server** (u otra base de datos compatible configurada en `application.properties`).
*   **Maven** (opcional, el proyecto incluye `mvnw`).

## ⚙️ Configuración e Instalación

1.  **Clonar el repositorio:**

    ```bash
    git clone https://github.com/tu-usuario/stockio.git
    cd stockio
    ```

2.  **Configurar la Base de Datos:**
    Asegúrate de tener un esquema de base de datos creado (ej. `stockio_db`) y actualiza las credenciales en `src/main/resources/application.properties` si es necesario:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/stockio
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```

3.  **Ejecutar la Aplicación:**
    Usa el wrapper de Maven incluido para compilar y ejecutar:

    ```bash
    ./mvnw spring-boot:run
    ```

    O en Windows:

    ```global
    .\mvnw.cmd spring-boot:run
    ```

4.  **Acceder:**
    Abre tu navegador y ve a: [http://localhost:8080](http://localhost:8080)

## 📦 Estructura del Proyecto

*   `src/main/java`: Código fuente Java (Controladores, Servicios, Repositorios, Modelos).
*   `src/main/resources/templates`: Vistas HTML (Thymeleaf).
*   `src/main/resources/static`: Recursos estáticos (CSS, JS, Imágenes).

## 👥 Autores

Desarrollado como parte del módulo M12 Proyecto de Desarrollo de Aplicaciones Web.

## 📄 Licencia

MIT License
