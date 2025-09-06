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

El proyecto sigue una arquitectura en capas limpia y modular, facilitando la separación de responsabilidades y la mantenibilidad:

---

## 🔒 Seguridad

La seguridad es un pilar fundamental de esta API:

* **Autenticación JWT:** Cada solicitud a un endpoint protegido requiere un token JWT válido en el encabezado `Authorization`.
* **Cookies HTTP-only:** El token (`token`) se gestiona de forma segura mediante una cookie marcada como `HttpOnly` y `Secure`, lo que impide su acceso vía JavaScript y protege contra ataques XSS.

* **Hashing de Contraseñas:** Las contraseñas de los usuarios nunca se almacenan en texto plano. Se utiliza `BCryptPasswordEncoder` para hashearlas antes de guardarlas en la base de datos.
* **Autorización Flexible:** Los endpoints están configurados para permitir o requerir autenticación según su función (ej. registro y lista de tópicos públicos, creación de tópicos protegida).

---

## 🖼️ Ejemplos en la Interfaz
* **Login:**
![TopicoNuevo](https://i.imgur.com/lsfdTp2.png)

* **Nuevo Topico:**
![TopicoNuevo](https://i.imgur.com/c0UokZk.png)

* **Lista de Topicos:**
![TopicoLista](https://i.imgur.com/Re0sIyF.png)

* **Nuevo Comentario:**
![Comentario](https://i.imgur.com/DVDAuPT.png)

* **Lista de Comentarios:**
![TopicoUno](https://i.imgur.com/C9z278a.png)

## 🏛 Despliegue en Docker

El proyecto está corriendo en un contenedor Docker y puedes probarlo usando el siguiente usuario de prueba:

**Usuario:** `Usuario`  
**Contraseña:** `Usuario123!`

🌐 Accede al sitio aquí: [ForoHub en Azure](http://20.163.60.146/forohub)

---

💡 **Tip:** Si quieres probar creando temas o comentarios, usa este usuario de prueba para no afectar datos reales.
