# 🚀 Aplicacion móvil para el control de inventario - Centro de Emprendimiento Uniandes

Una aplicación móvil desarrollada en Kotlin utilizando Jetpack Compose para la gestión de inventarios.
Permite a los usuarios registrar, actualizar y consultar productos de manera sencilla, ofreciendo una interfaz moderna, intuitiva y optimizada para dispositivos Android.

---

## 📚 Versión inicial v1.0

* **Gestión Completa de Productos (CRUD):**
    * Creación de nuevos productos con nombre, categoría, cantidad y precio.
    * Consulta del detalle de un producto específico por ID o código.
    * Actualización de productos existentes (nombre, cantidad, precio, categoría).
    * Eliminación de productos del inventario.
* **Gestión de Emprededores:**
    * Registro de emprendedores con sus datos básicos (nombre, correo, emprendimiento).
    * Visualización de la lista de emprendedores registrados.
    * Asociación automática de los productos con el emprendedor que los gestiona.
* **Gestión de Promociones:**
    * Creación de nuevas promociones con nombre, descripción, tipo (descuento, 2x1, etc.) y período de vigencia.
    * Cálculo automático del total de la venta, aplicando promociones activas si corresponden.
    * Asociación automática de promociones con los productos correspondientes.
* **Ventas:**
    * Registro de nuevas ventas indicando productos, cantidades y precios aplicados.
    * Visualización de todas las promociones activas y pasadas.
    * Consulta del historial de ventas con filtros por fecha, producto o cliente.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Kotlin
* **Framework:** Jetpack Compose
* **Arquitectura:** MVVM (Model-View-ViewModel)

---

## 🏛️ Archivos de la linea de base

| Archivo / Carpeta       | Descripción |
|--------------------------|-------------|
| `.idea/`                | Configuración del proyecto para Android Studio/IntelliJ IDEA (preferencias del entorno, no afecta la lógica). |
| `app/`                  | Carpeta principal de la aplicación Android. Contiene el código fuente en Kotlin, recursos (layouts, imágenes, strings) y configuración del módulo. |
| `gradle/`               | Archivos y configuraciones internas del sistema de construcción Gradle. |
| `.gitignore`            | Define qué archivos y carpetas deben ignorarse en Git (ejemplo: builds temporales, configuraciones locales). |
| `README.md`             | Documento de introducción al proyecto, incluye instrucciones de instalación, uso y guías para desarrolladores. |
| `build.gradle.kts`      | Archivo de configuración principal de Gradle (Kotlin DSL). Define dependencias, plugins y opciones de compilación. |
| `gradle.properties`     | Variables y configuraciones globales para Gradle (ejemplo: versión de JVM, flags de compilación). |
| `gradlew`               | Script de Gradle Wrapper para Linux/macOS. Permite ejecutar Gradle sin necesidad de instalarlo en el sistema. |
| `gradlew.bat`           | Script de Gradle Wrapper para Windows. |
| `settings.gradle.kts`   | Define los módulos incluidos en el proyecto (por ejemplo, `:app`) y configuración inicial de Gradle. |

---

## 📌 Próximo cambio aprobado (para v1.1)

Los siguientes cambios han sido aprobados para la próxima versión:

- Implementar generación de **PDF de la venta**.
- Agregar **detalles de la vista de ventas**.
- Incluir **paginado en las listas** para mejorar el rendimiento y la navegación.

---

