# PLANTAZON

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Álvaro Fuente González | a.fuente.2023@alumnos.urjc.es | alvaroSource |
| Darío García Gómez | d.garciago.2023@alumnos.urjc.es | dariogarciagomez |
| Arturo Vinuesa Domínguez | a.vinuesad.2023@alumnos.urjc.es | arturovinuesaa |
| Eduardo Fernández Sanz | e.fernandezs.2023@alumnos.urjc.es | edufdezz |

---

## 🎭 **Preparación 1: Definición del Proyecto**

### **Descripción del Tema**
La aplicación creada se trata de un e-commerce destinado a la jardinería, la aplicación muestra distintos productos de diferentes categorías a elegir de temática de jardinería y botánica.
[Escribe aquí una descripción breve y concisa de qué trata tu aplicación, el sector al que pertenece y qué valor aporta al usuario].

### **Entidades**
Indicar las entidades principales que gestionará la aplicación y las relaciones entre ellas:

1. **Usuario**
2. **Producto**
3. **Carrito**
4. **Categoría**
5. **Etiqueta**
6. **Pedido**
7. **Reseña**

**Relaciones entre entidades:**
- Usuario - Carrito: Un usuario puede tener un carrito (1:1).
- Usuario - Pedido: Un usuario puede tener varios pedidos (1:N).
- Carrito - Producto: Un carrito puede tener varios productos (1:N).
- Producto - Etiqueta: Un producto puede tener varias etiquetas y una etiqueta puede pertenecer a varios productos (N:M).
- Pedido - Producto: Un pedido puede tener varios productos (1:N).
- Categoria - Producto: Una categoría puede tener varios productos (1:N).
- Producto - Reseña: Un producto puede tener varias reseñas (1:N).
### **Permisos de los Usuarios**

* **Usuario Anónimo**: 
  - Permisos: Visualización de catálogo, búsqueda de productos, registro, gestion de productos del carrito.
  - Es dueño de su Carrito.

* **Usuario Registrado**: 
  - Permisos: Gestión de perfil, realización y gestión de pedidos, crear reseñas. 
  - Es dueño de: Sus propios Pedidos, su Perfil de Usuario, sus Reseñas.

* **Administrador**: 
  - Permisos: Gestión completa de productos (CRUD), visualización de estadísticas, moderación de contenido, gestión de usuarios.
  - Es dueño de: Productos, Categorías, puede gestionar todos los Pedidos y Usuarios

### **Imágenes**
Indicar qué entidades tendrán asociadas una o varias imágenes:

- **Usuario**: Una imagen de avatar por usuario
- **Producto**: Múltiples imágenes por producto (carrusel)
- **Reseña**: Puede tener una imagen cada reseña.

### **Gráficos**
Indicar qué información se mostrará usando gráficos y de qué tipo serán:

- **Productos más comprados**: Ventas mensuales dividas por categoría - Gráfico de tarta.
- **Productos por etiqueta**: Etiquetas más vendidas - Gráfico de tarta.
- **Ventas mensuales**: Evolución de ventas por mes - Gráfico de barras.
- **Relación visitas-compra**: Número de compras en comparación a usuarios que han visitado la página - Gráfico de líneas.
- **Gráfico de reseñas**: Reseñas de un producto a lo largo del tiempo - Gráfico de líneas.

### **Tecnología Complementaria**
Indicar qué tecnología complementaria se empleará:

- Envío de correos electrónicos automáticos mediante JavaMailSender.
- Generación de PDFs de facturas usando iText o similar.
- Sistema de autenticación OAuth2.
- Generar gráficas con JFreeChart.

### **Algoritmo o Consulta Avanzada**
Indicar cuál será el algoritmo o consulta avanzada que se implementará:

- **Algoritmo**: Sistema de recomendaciones basado en el historial de compras del usuario.
- **Descripción**: Analiza los productos comprados previamente y sugiere productos similares o complementarios basandose en las etiquetas del producto.
- **Alternativa**: Consulta compleja que agrupe ventas por reseñas.

---

## 🛠 **Preparación 2: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/navigation-diagram.png)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![Página Principal](images/home-page.png)

> Página de inicio que muestra el catálogo de productos con filtrado por categorías (Cuidado, Plantas, Suelo, Herramientas, Inspiración, Guardados). Incluye barra lateral de navegación, barra de búsqueda superior y acceso al carrito y perfil de usuario. Los productos se muestran en tarjetas con imagen, nombre, precio y opción de añadir al carrito.

#### **2. Página de Inicio de Sesión / Login**
![Página de Login](images/login-page.png)

> Página de autenticación que permite a los usuarios iniciar sesión o registrarse en la aplicación. Incluye formularios con validación para email y contraseña, opción de suscripción a novedades, y una interfaz con pestañas deslizantes para alternar entre inicio de sesión y registro.

#### **3. Página de Detalle de Producto**
![Página de Producto](images/product-page.png)

> Página que muestra información detallada de un producto específico: galería de imágenes con navegación por puntos, información del producto (nombre, descripción, etiquetas, precio con descuento), selector de cantidad, botones de compra y añadir al carrito. Incluye sección de productos recomendados y valoraciones de usuarios con gráficos de barras y comentarios.

#### **4. Página del Carrito**
![Página de Carrito](images/cart-page.png)

> Página que muestra el carrito de compra con los productos seleccionados. Cada producto incluye imagen, nombre, tamaño, precio, controles de cantidad y opción de eliminar. Panel lateral con resumen del pedido (subtotal, envío, total) y botón para tramitar el pedido.

#### **5. Página de Pedidos**
![Página de Pedidos](images/order-page.png)

> Página que muestra el historial de pedidos del usuario. Cada pedido incluye número de orden, fecha, estado (entregado, en proceso, etc.), precio total, cantidad de productos y botón expandible para ver detalles. Los detalles incluyen imágenes de productos, cantidades, precios individuales, resumen con subtotal y envío, y opciones para volver a comprar o dejar opinión.

#### **6. Página de Perfil de Usuario**
![Página de Usuario](images/user-page.png)

> Página de gestión del perfil del usuario que permite editar datos personales (nombre de usuario, correo, contraseña, dirección de envío) y configurar preferencias de cuenta (notificaciones por email, modo oscuro, recibir ofertas). Incluye botones para guardar cambios o eliminar cuenta.

#### **7. Página de Administrador**
![Página de Administrador](images/admin-page.png)

> Panel de administración que permite gestionar productos (añadir, modificar, eliminar), visualizar estadísticas de ventas mediante gráficos, gestionar usuarios y moderar contenido. Incluye barra de búsqueda para productos y acceso rápido a todas las funcionalidades administrativas.

---

## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://youtu.be/UTMMdqvSfkI)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

![Diagrama de navegación](images/navigation-diagram-P1.png)

#### **Capturas de Pantalla Actualizadas**

**1. Páginas de administrador**  
> La página de administrador fue dividida en dos para poder diferenciar gestión de usuarios y gestión de productos.

**1.1 Sección de administrador**  
![Página de administrador](images/admin-page-P1.png)  
> Página de administración que permite visualizar estadísticas mediante gráficos y gestionar usuarios y moderar contenido.

**1.2 Gestor de productos**  
![Página de gestor de productos](images/admin-products-page.png)  
> Página de administración en la que se pueden añadir nuevos productos, gestionar categorías (añadirlas, quitarlas y editarlas) y gestionar productos. 


### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven** + **Springboot**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1.**Clonar el repositorio**
   ```bash
   git clone https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4.git
   cd practica-daw-2025-26-grupo-4
   ```

2.**Abrir el proyecto en VSCode**
  - Abre la carpeta del proyecto en VSCode.  
  - Navega a la clase principal del proyecto:
    src\main\java\es\urjc\daw04\Daw04Application.java

3.**Ejecutar la aplicación**
  - Haz clic derecho sobre `DAW04Application.java` → **Run 'DAW04Application.main()'**  
  o abre la clase y pulsa **Run Main** en la parte superior.  
  - VSCode iniciará el servidor Spring Boot y mostrará los logs en la terminal integrada.
  - Nosotros hemos usado también la extensión Spring Boot Dashboard en vscode para facilitar la ejecución.

4.**Abrir en navegador**
https://localhost:8443

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin`
- **Usuario Registrado**: usuario: `user`, contraseña: `user`

### **Diagrama de Entidades de Base de Datos**

Diagrama mostrando las entidades, sus campos y relaciones:

![Diagrama Entidad-Relación](images/database-diagram.jpeg)

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/class-diagram-P1.png)  

> Este diagrama detalla las clases principales del backend y frontend, incluyendo controladores, servicios y templates HTML asociados a cada funcionalidad.

### Participación de Miembros en la Práctica 1

#### Alumno 1 - Álvaro Fuente Gonzalez

Responsable de las funcionalidades de administración de productos y categorías, sistema de recomendaciones e implementación total de AJAX.

| Nº | Commits | Files |
|----|---------|-------|
|1| [Recommendations page and algorithm implemented](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/7f1e8d1) | RecommendationController.java, RecommendationPack.java, CartItemRepository.java, OrderRepository.java‎, RecommendationService.java |‎
|2| [AJAX completly implemented](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/5459719) | AdminController.java, HomeController.java, ShopController.java, ProductRepository.java, ReviewRepository.java, ProductService.java, ReviewService.java, UserService.java, pagination.js, fragments/home-products.html, fragments/orders.html, fragments/reviews.html, fragments/admin-users.html, fragments/admin-products-rows.html |
|3| [Admin page: GlobalModelAdvice, Image entity, admin-products page, UserService](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/662561a) | AdminController.java, GlobalModelAdvice.java, ImageController.java, Image.java, ImageService.java, UserService.java, SampleDataService.java, WebSecurityConfig.java, admin-products.html, admin.css |
|4| [Category CRUD operations](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/97c6635) | AdminController.java, CategoryService.java, admin-products.html |
|5| [Fixed Security](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/486063e) | AuthController.java, User.java, UserRepository.java, WebSecurityConfig.java, RepositoryUserDetailsService.java, CSRFHandlerConfiguration.java |

---

#### Alumno 2 - Darío García Gómez

Responsable del servicio de pedidos, gráficas del admin, carrito con cookies, gestión de reseñas con usuarios eliminados y modelos de carrito.

| Nº | Commits | Files |
|----|---------|-------|
|1| [Cookies implemented in cart](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/acf8a0f) | AdminController.java, AuthController.java, HomeController.java, ShopController.java, Cart.java, CartItem.java, CartService.java, cart.html |
|2| [Graphics and charts](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/e627acb) | AdminController.java, Review.java, OrderRepository.java, ReviewRepository.java, OrderService.java, ReviewService.java, admin.html |
|3| [Reviews in database and user samples](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/eb66420) | ShopController.java, Product.java, Review.java, SampleDataService.java, UserService.java, application.properties, product.html |
|4| [Delete user and review continues](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/e447b56) | AdminController.java, Review.java, User.java, ReviewRepository.java, SampleDataService.java, UserService.java |
|5| [Cart models and services](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/188daea) | Cart.java, CartItem.java, CartService.java, ShopController.java, ProductRepository.java, application.properties |

---

#### Alumno 3 - Eduardo Fernández Sanz

Responsable de la lógica de carrito, pedidos, reseñas y correcciones generales de frontend.

| Nº | Commits | Files |
|----|---------|-------|
|1| [Implemented add a review function](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/3282c7f) | AdminController.java, ShopController.java, Product.java, ReviewRepository.java, WebSecurityConfig.java, ReviewService.java, order.js, order.css, product.css, fragments/orders.html, order.html, product.html |
|2| [Fixed order page access](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/0d8b94d) | ShopController.java, WebSecurityConfig.java, cart.html, order.html |
|3| [Address form](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/6632b24) | AuthController.java, User.java, WebSecurityConfig.java, user.js, user.css, user.html
|4| [Search and footer fixes](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/700e886) | HomeController.java, ProductRepository.java, ProductService.java, components.css, home.css, cart.html, home.html, login.html, order.html, product.html, user.html |
|5| [Random product suggestion](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/257716b) | ShopController.java |

---

#### Alumno 4 - Arturo Vinuesa Dominguez

Responsable de seguridad, autenticación, usuarios y control de acceso.

| Nº | Commits | Files |
|----|---------|-------|
|1| [Admin can ban and edit users](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/b957e2d) | AdminController.java, User.java, RepositoryUserDetailsService.java, admin.css, admin.html |
|2| [User screen and password change](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/56783e3) | AuthController.java, user.html |
|3| [Welcome email on register](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/7c48375) | EmailService.java, AuthController.java, pom.xml, application.properties, login.html |
|4| [User registration and login](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/6b0238e) | AuthController.java, WebSecurityConfig.java, SampleDataService.java, login.css, header.html, login.html |
|5| [Security database](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/f80e99e) | pom.xml, AuthController.java, User.java, UserRepository.java, CSRFHandlerConfiguration.java, RepositoryUserDetailsService.java, WebSecurityConfig.java, UserService.java (eliminado) |
---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube]([https://www.youtube.com/watch?v=x91MPoITQ3I](https://youtu.be/Gs_mfhSoGpQ))**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/p2/diagrama-clases.drawio.svg)

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**
- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)

#### **Pasos para ejecutar con docker-compose:**

1. **Clonar el repositorio** (si no lo has hecho ya):
   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   cd [repositorio]
   ```

2. **Ejecutar la aplicación con Docker Compose**:
   ```bash
   cd docker
   docker compose pull
   docker compose up -d
   ```

3. **Comprobar que los contenedores están levantados**:
   ```bash
   docker compose ps
   ```

4. **Parar y eliminar los contenedores al terminar**:
   ```bash
   docker compose down
   ```

### **Construcción de la Imagen Docker**

#### **Requisitos:**
- Docker instalado en el sistema

#### **Pasos para construir y publicar la imagen:**

1. **Navegar al directorio de Docker**:
   ```bash
   cd docker
   ```

2. **Iniciar sesión en DockerHub**:
   ```bash
   docker login -u [usuario_dockerhub]
   ```

3. **Construir la imagen local**:
   ```bash
   ./create_image.sh [nombre_imagen] [tag]
   ```

4. **Publicar la imagen en DockerHub**:
   ```bash
   ./publish_image.sh [usuario_dockerhub] [nombre_imagen] [tag]
   ```

5. **Publicar el docker-compose como OCI Artifact**:
   ```bash
   ./publish_docker-compose.sh [usuario_dockerhub] [nombre_repositorio_compose] [tag]
   ```

### **Despliegue en Máquina Virtual**

#### **Requisitos:**
- Acceso a la máquina virtual (SSH)
- Clave privada para autenticación
- Conexión a la red correspondiente o VPN configurada

#### **Pasos para desplegar:**

1. **Conectar a la máquina virtual**:
   ```bash
   ssh -i ssh-keys/appWeb04.key vmuser@10.100.139.52
   ```
   
   Ejemplo:
   ```bash
   ssh -i ssh-keys/appWeb04.key vmuser@appWeb04.dawgis.etsii.urjc.es
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **URL de la Aplicación Desplegada**

🌐 **URL de acceso**: `https://10.100.139.52:8443/`

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin |
| Usuario Registrado | user | user |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - Álvaro Fuente González**

Responsable de la creación de múltiples endpoints REST (productos, carritos, reviews, imágenes) y sus respectivos DTOs, creación de REST Controllers base, así como la configuración inicial de Spring Security con JWT.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Practice 2 v1.2 Added rest controller to product, review, cart and image](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/ba535bfa9144c8bb2480bf7d2f86c52f2a21268b)  | ProductRestController.java, ReviewRestController.java, CartRestController.java   |
|2| [Added DTO to all entities](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/e7480ab)  | ProductDTO.java, CartDTO.java, ReviewDTO.java, UserDTO.java   |
|3| [Image controller and DTO added to Products](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/e59c742)  | ImageController.java, ProductDTO.java   |
|4| [Pageable Products and Reviews](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/8480e0d)  | ProductService.java, ReviewService.java, ProductRestController.java   |
|5| [Practice 2 v 1.1 Completed Security](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/de8a1f8)  | LoginController.java, WebSecurityConfig.java   |

---

#### **Alumno 2 - Darío García Gómez**

Responsable del refactoring de REST Controllers, del desarrollo de la API REST de pedidos y recomendaciones, y de la integración de las imágenes para los usuarios y productos.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Practice  2 v1.5 Order API REST finished](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/6d035c5f241f11997b97657a831ae6e10ad3bdfb)  | OrderRestController.java, OrderDTO.java, OrderMapper.java   |
|2| [Practice 2 v1.3 Added rest controller to recommendation](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/ff37ce839078168712ade7ccc6d7c79bd79844d2)  | RecommendationController.java, RecommendationPackDTO.java   |
|3| [Practice 2 v1.15 Images problem solved. User image implemented](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/28870ce)  | AdminController.java, AuthController.java, UserRestController.java   |
|4| [Practice 2 v1.11 Rest refactoring](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/5696e8d)  | CartRestController.java, HomeRestController.java, LoginRestController.java   |
|5| [Practice 2 v1.18 two new requestMatchers](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/454ef5e)  | WebSecurityConfig.java   |

---

#### **Alumno 3 - Eduardo Fernández Sanz**

Responsable de la parte de Recomendaciones, configuración para el despliegue con Docker y de asegurar consistencia en vistas.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Practice 2 v1.19 Updated recommendation algorithm](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/e0d1d6670f2ad366bb2edee5567b2903353b0596)  | RecommendationController.java, RecommendationService.java   |
|2| [Practice 2 v1.16 Fixed infinte scroll not working correctly](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/bcf840e8a07c933969ab06db7b386ef958f54ea4)  | admin-products.html, admin.html, home.html   |
|3| [Practice 2 v1.6 Created REST HomeController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/6d974dc09bc301dd4e5358162eacc6dc993c8e71)  | HomeController.java, RecommendationController.java   |
|4| [Practice 2 v1.4 Moved Docker files to Docker folder](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/c5020a26c09461850a6e760c7511c355f5cc9122)  | docker/Dockerfile, create_image.sh, docker-compose.yml   |
|5| [Practice 2 v.1.4 Added dockerfile and docker-compose](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/d2f9a723a5489f3b9f2589f9783a5327e8cc8c0a)  | Dockerfile, docker-compose.yml, application.properties   |

---

#### **Alumno 4 - Arturo Vinuesa Domínguez**

Responsable de la API REST para el control de cuentas de usuario, perfil, direcciones y aspectos de seguridad y versionado.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [P2-v1.7: Refactor and stabilize REST API migration with /api/v1 versioning...](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/c7449e6)  | AuthController.java, LoginController.java, CartController.java   |
|2| [Practice 2 v1.7 Add user account REST API with DTOs](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/5d2366b)  | UserRestController.java, UserAccountUpdateRequestDTO.java   |
|3| [Practice 2 v1.8](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/015641e)  | AdminRestController.java, AdminCategoryRequestDTO.java   |
|4| [Practice 2 v 1.10 Admin-&-auth-controller](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/d021dcc)  | AdminRestController.java, LoginRestController.java   |
|5| [Practice 2 v1.17](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4/commit/24477bf)  | AdminRestController.java, ReviewController.java   |


---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](URL_del_video)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**
- **Node.js**: versión 18.x o superior
- **npm**: versión 9.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**
   
   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/)
   
   Verifica la instalación:
   ```bash
   node --version
   npm --version
   ```

2. **Clonar el repositorio** (si no lo has hecho ya)
   ```bash
   git clone https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-4.git
   cd practica-daw-2025-26-grupo-4
   ```

3. **Navegar a la carpeta del proyecto React**
   ```bash
   cd frontend
   ```

4. **Instalar dependencias**

   ```bash
   npm install
   ```

5. **Arrancar el frontend en desarrollo**

   ```bash
   npm run dev
   ```

   La aplicación estará disponible en la URL que indique el servidor de desarrollo, normalmente `http://localhost:5173/`.

6. **Generar la versión de producción**

   ```bash
   npm run build
   ```

7. **Publicar el build en el backend local**

   ```bash
   npm run deploy:local
   ```

   Este comando copia el contenido compilado en `backend/src/main/resources/static/new/` para que el backend sirva la SPA integrada.

#### **Ejecución recomendada**

Para probar la aplicación completa, arranca primero el backend y después el frontend. El frontend consume la API REST existente y necesita que el backend esté disponible para cargar catálogo, sesión, carrito, pedidos y administración.


### **Diagrama de Clases y Templates de la SPA**

Diagrama que muestra los componentes React, hooks personalizados, servicios y su relación con las pantallas principales:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - Álvaro Fuente González**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - Darío García Gómez**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - Eduardo Fernández Sanz**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - Arturo Vinuesa Domínguez**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

