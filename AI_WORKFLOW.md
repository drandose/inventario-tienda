# AI_WORKFLOW

## Herramienta

He usado Junie dentro de IntelliJ IDEA, desde el panel Air, con el modelo gratuito Junie Lite.

## Decisiones de arquitectura

- Tema: inventario de tienda, con las tablas categoría, producto y proveedor.
- Servidor: Payara Micro 7 con Java 21, porque el profesor indicó usar Payara.
- Acceso a datos con JDBC y PreparedStatement. Base de datos MySQL.
- API con JAX-RS e interfaz con HTML, CSS y JavaScript.

## Registro de trabajo

### 1. Crear el pom.xml

Prompt:

> Crea un pom.xml para una aplicación web con Maven y Java 21. El groupId es com.tienda, el artifactId inventario-tienda
> y el packaging war.
> Dependencias:
> - jakarta.platform:jakarta.jakartaee-web-api:11.0.0 con scope provided
> - com.mysql:mysql-connector-j:26.7.0
> - org.junit.jupiter:junit-jupiter:5.14.4 con scope test
>
> No añadas nada más ni uses git.

Puse las versiones exactas en el prompt para que el agente no eligiera versiones antiguas.

Resultado: Junie creó el pom.xml con lo pedido y no añadió nada más.

Revisión: faltaba la codificación del proyecto. Prompt de corrección:

> He revisado el pom.xml y falta la codificación. Añade en properties la propiedad project.build.sourceEncoding con
> valor UTF-8 y no cambies nada más. Después ejecuta mvn package y dime si termina bien.

Resultado: Junie añadió solo esa propiedad. El comando mvn package terminó bien y generó el archivo
inventario-tienda-1.0-SNAPSHOT.war en la carpeta target.

### 2. Crear el .gitignore

Prompt:

> Crea un archivo .gitignore en la raíz del proyecto que ignore target/, .idea/, los archivos *.iml, el archivo .env y
> .DS_Store. No uses git.

Resultado: Junie creó el .gitignore con las cinco reglas pedidas. No hizo falta corregir nada.

### 3. Crear el script de la base de datos

Decisión: una categoría tiene muchos productos (relación 1:N) y un producto puede tener varios proveedores (relación N:
M) mediante la tabla intermedia producto_proveedor. No se puede borrar una categoría que tenga productos (ON DELETE
RESTRICT). Al borrar un producto o un proveedor se borran sus relaciones (ON DELETE CASCADE).

Prompt:

> Crea el archivo db/init.sql para MySQL. Empieza con SET NAMES utf8mb4. No pongas CREATE DATABASE ni USE, porque la
> base de datos la crea Docker.
> Tablas:
> - categoria: id (clave primaria autoincremental), nombre (obligatorio y único).
> - proveedor: id (clave primaria autoincremental), nombre (obligatorio), telefono, email.
> - producto: id (clave primaria autoincremental), codigo (obligatorio y único), nombre (obligatorio), precio (decimal,
>   mayor que 0), stock (entero, no negativo), categoria_id (clave foránea a categoria con ON DELETE RESTRICT y ON
>   UPDATE CASCADE).
> - producto_proveedor: producto_id y proveedor_id como clave primaria compuesta, las dos claves foráneas con ON DELETE
>   CASCADE.
>
> Después añade datos de prueba: 3 categorías, 3 proveedores, 6 productos y varias relaciones entre productos y
> proveedores.
> No crees ningún otro archivo ni uses git.

Resultado: Junie creó db/init.sql con las cuatro tablas, las restricciones y los datos de prueba. Tras revisarlo no hizo
falta corregir nada.

### 4. Docker Compose con la base de datos

Prompt:

> Crea un docker-compose.yml con dos servicios. El servicio de la aplicación todavía no.
> - db: imagen mysql:8.4. Las variables MYSQL_ROOT_PASSWORD, MYSQL_DATABASE, MYSQL_USER y MYSQL_PASSWORD se leen del
>   archivo .env con los nombres DB_ROOT_PASSWORD, DB_NAME, DB_USER y DB_PASSWORD. Usa un volumen con nombre db_data
>   en
>   /var/lib/mysql. Monta db/init.sql en /docker-entrypoint-initdb.d/ en solo lectura. Añade un healthcheck con
>   mysqladmin ping. No publiques ningún puerto de db.
> - adminer: imagen adminer:5, con el puerto 8081 del equipo hacia el 8080 del contenedor, y que espere a que db esté
>   healthy.
>
> Los dos servicios van en una red llamada red_interna.
> Crea también .env.example con esas cuatro variables y valores de ejemplo. No crees el archivo .env ni uses git.

Resultado: Junie creó el docker-compose.yml con MySQL y Adminer y el archivo .env.example.

Corrección manual: Junie había puesto nombres fijos al volumen y a la red (name: db_data y name: red_interna). Borré
esas dos líneas para que Docker ponga delante el nombre del proyecto y no choquen con otros proyectos del mismo equipo.

Comprobación: copié .env.example a .env y ejecuté docker compose up -d. El contenedor de MySQL quedó en estado healthy.
Entré en Adminer (http://localhost:8081) y estaban las cuatro tablas con los datos de prueba.

### 5. Primera parte de la aplicación

Prompt:

> Crea la primera parte de la aplicación con JAX-RS (Jakarta EE 11), sin base de datos todavía:
> - Clase com.tienda.inventario.api.ConfiguracionApi que extienda Application con @ApplicationPath("/api").
> - Clase com.tienda.inventario.api.EstadoResource con un método GET en la ruta /estado que devuelva el JSON {"estado":
>   "ok"}.
> - Página src/main/webapp/index.html con el título "Inventario de tienda".
>
> Después ejecuta mvn package y dime si termina bien. No uses git.

Resultado: Junie creó las dos clases y la página. El comando mvn package terminó con BUILD SUCCESS. No hizo falta
corregir nada.

### 6. Dockerfile y servicio de la aplicación

Prompt:

> Crea un Dockerfile multi-stage:
> - Primera etapa con la imagen maven:3.9-eclipse-temurin-21 que copie pom.xml y src y ejecute mvn package -DskipTests.
> - Segunda etapa con la imagen payara/micro:7.2026.11 que copie el .war generado a
>   /opt/payara/deployments/inventario-tienda.war y arranque con
>   CMD ["--deploy", "/opt/payara/deployments/inventario-tienda.war", "--contextroot", "ROOT", "--noCluster"].
>
> Añade al docker-compose.yml el servicio app: se construye con ese Dockerfile, publica el puerto 8080, está en
> red_interna, espera a que db esté healthy y recibe las variables de entorno DB_URL (jdbc:mysql://db:3306/ seguido del
> nombre de la base de datos), DB_USER y DB_PASSWORD tomadas del .env.
> Crea también un .dockerignore con target, .git, .idea y .env. No cambies nada más ni uses git.

Resultado: Junie creó el Dockerfile con dos etapas, añadió el servicio app al docker-compose.yml y creó el
.dockerignore. No hizo falta corregir nada.

Comprobación: ejecuté docker compose up --build -d y arrancaron los tres contenedores. La
dirección http://localhost:8080/api/estado respondió {"estado":"ok"}.

### 7. Conexión con la base de datos y lectura de categorías

Prompt:

> Conecta la aplicación con la base de datos y crea la lectura de categorías por capas:
> - src/main/webapp/WEB-INF/web.xml (Jakarta EE 11) con un data-source: name java:app/jdbc/inventario, class-name
>   com.mysql.cj.jdbc.MysqlDataSource, url ${ENV=DB_URL}, user ${ENV=DB_USER} y password ${ENV=DB_PASSWORD}.
> - src/main/webapp/WEB-INF/beans.xml con bean-discovery-mode="annotated".
> - modelo.Categoria con id y nombre.
> - dto.CategoriaDTO con id y nombre.
> - excepcion.AccesoDatosException (RuntimeException).
> - dao.CategoriaDao (@ApplicationScoped) que reciba el DataSource con @Resource(lookup = "java:app/jdbc/inventario") y
>   tenga un método listar() con PreparedStatement y try-with-resources. Si hay SQLException lanza
>   AccesoDatosException.
> - servicio.CategoriaServicio (@ApplicationScoped) que use el DAO y devuelva una lista de CategoriaDTO.
> - api.CategoriaResource (@RequestScoped, ruta /categorias) con un GET que devuelva la lista en JSON.
>
> Todo dentro del paquete com.tienda.inventario. Después ejecuta mvn package. No uses git.

Resultado: Junie creó el web.xml, el beans.xml y las clases de cada capa. El proyecto compilaba, pero al probarlo en
Docker la dirección /api/categorias daba error 404.

Error 1: en el registro de Payara (docker compose logs app) aparecía que el web.xml no era válido, porque la url del
data-source tiene que empezar por jdbc: y yo la había pedido entera en una variable. Prompt de corrección:

> Al desplegar en Payara la aplicación falla con este error del web.xml:
> cvc-pattern-valid: Value '${ENV=DB_URL}' is not facet-valid with respect to pattern 'jdbc:(.*):(.*)'
> La url del data-source tiene que empezar por jdbc:. Cambia la url a jdbc:mysql://${ENV=DB_HOST}:${ENV=DB_PORT}/${ENV=
> DB_NAME}. En el servicio app del docker-compose.yml quita DB_URL y pon DB_HOST con valor db, DB_PORT con valor 3306 y
> DB_NAME tomado del .env. No cambies nada más ni uses git.

Error 2: la aplicación ya desplegaba, pero la conexión con MySQL fallaba con "Communications link failure". En el
registro se veía que el fallo estaba en la negociación SSL. Prompt de corrección:

> La aplicación ya despliega, pero al pedir /api/categorias falla la conexión con MySQL: "Communications link failure"
> con "Broken pipe" durante el handshake SSL (sun.security.ssl.SSLSocketImpl.startHandshake). Como la app y la base de
> datos están en la misma red interna de Docker, desactiva el cifrado de esa conexión: añade al final de la url del
> data-source en web.xml los parámetros ?sslMode=DISABLED&allowPublicKeyRetrieval=true (recuerda que en XML el & se
> escribe &amp;). No cambies nada más ni uses git.

Decisión: desactivar el cifrado entre la aplicación y MySQL porque los dos contenedores están en la misma red interna de
Docker y MySQL no publica ningún puerto. En un entorno real se mantendría el cifrado.

Comprobación: tras reconstruir con docker compose up --build -d, la dirección http://localhost:8080/api/categorias
devolvió las tres categorías en JSON.

### 8. CRUD completo de categorías

Prompt:

> Completa el CRUD de categorías manteniendo las capas:
> - Excepciones nuevas en excepcion: ValidacionException, NoEncontradoException y ConflictoException (todas
>   RuntimeException).
> - CategoriaDao: añade buscarPorId, insertar (devuelve el id generado), actualizar y borrar, con PreparedStatement y
>   try-with-resources. Si MySQL lanza SQLIntegrityConstraintViolationException (nombre repetido o categoría con
>   productos) lanza ConflictoException; para el resto de SQLException, AccesoDatosException.
> - CategoriaServicio: obtener, crear, actualizar y borrar usando CategoriaDTO. Valida que el nombre no esté vacío y no
>   pase de 100 caracteres (ValidacionException) y lanza NoEncontradoException si el id no existe.
> - CategoriaResource: GET /categorias/{id}, POST (responde 201 con la categoría creada), PUT /categorias/{id} (200) y
>   DELETE /categorias/{id} (204).
> - Clase api.ErrorMapper (@Provider, ExceptionMapper<RuntimeException>) que devuelva un JSON {"error": "mensaje"} con
>   400 para ValidacionException, 404 para NoEncontradoException, 409 para ConflictoException y 500 para el resto. Si
>   la
>   excepción es una WebApplicationException devuelve su propia respuesta.
>
> Después ejecuta mvn package. No uses git.

Resultado: Junie completó el DAO, el servicio y el resource, y creó las tres excepciones y el ErrorMapper. Además añadió
por su cuenta dos tests de validación del nombre, que no estaban en el prompt.

Comprobación con curl tras reconstruir los contenedores:

- Crear la categoría "Redes" devolvió 201.
- Crear una categoría con el nombre vacío devolvió 400.
- Pedir la categoría 999 devolvió 404.
- Borrar la categoría 1, que tiene productos, devolvió 409.

### 9. CRUD de productos

Prompt:

> Crea el CRUD de productos siguiendo el mismo patrón por capas que categorías:
> - modelo.Producto con id, codigo, nombre, precio (BigDecimal), stock y categoriaId.
> - dto.ProductoDTO con esos campos más categoriaNombre, que es solo de lectura y se obtiene con un JOIN.
> - dao.ProductoDao con listar, buscarPorId, insertar, actualizar y borrar. Si el código está repetido lanza
>   ConflictoException.
> - servicio.ProductoServicio con estas validaciones (ValidacionException): código y nombre obligatorios, precio mayor
>   que 0, stock no negativo y que la categoría exista. Lanza NoEncontradoException si el id no existe.
> - api.ProductoResource en /productos con GET, GET /{id}, POST (201), PUT /{id} y DELETE /{id} (204).
>
> Después ejecuta mvn package. No uses git.

Resultado: Junie creó las clases de cada capa siguiendo el patrón de categorías. También añadió, sin estar en el prompt,
tests de las validaciones de código, nombre, precio y stock. Con ellos pasan 7 tests.

Comprobación con curl:

- Pedir el producto 1 devolvió 200, con el nombre de su categoría.
- Crear el producto RED-001 devolvió 201.
- Crear un producto con precio negativo devolvió 400.
- Crear un producto con un código repetido devolvió 409.

### 10. CRUD de proveedores y relación con productos

Prompt:

> Crea el CRUD de proveedores con el mismo patrón por capas (modelo.Proveedor, dto.ProveedorDTO, dao.ProveedorDao,
> servicio.ProveedorServicio y api.ProveedorResource en /proveedores). El nombre es obligatorio y, si hay email, debe
> contener una @ (ValidacionException).
> Añade también la relación entre productos y proveedores:
> - dao.ProductoProveedorDao con listarProveedoresDeProducto, borrarPorProducto e insertar.
> - En ProductoServicio, un método asignarProveedores(productoId, lista de ids de proveedor) anotado con @Transactional
>   (jakarta.transaction) que compruebe que el producto y los proveedores existen, borre las relaciones anteriores e
>   inserte las nuevas, para que se guarde todo o nada.
> - En ProductoResource: GET /productos/{id}/proveedores y PUT /productos/{id}/proveedores, que recibe un JSON con la
>   lista de ids.
>
> Después ejecuta mvn package. No uses git.

Resultado: Junie creó el CRUD de proveedores, el ProductoProveedorDao y los dos endpoints nuevos. El método
asignarProveedores lleva @Transactional. También añadió tests; en total pasan 12.

Comprobación con curl:

- Pedir los proveedores del producto 1 devolvió 200 con los proveedores 1 y 2.
- Cambiarlos por los proveedores 2 y 3 devolvió 204.
- Volver a pedirlos devolvió los proveedores 2 y 3.
- Intentar asignar el proveedor 999, que no existe, devolvió 404.

### 11. Interfaz web: base y productos

Prompt:

> Crea la interfaz web en src/main/webapp con HTML5 semántico, CSS y JavaScript moderno (fetch con async/await), sin
> librerías externas:
> - index.html con cabecera, navegación con tres secciones (Productos, Categorías, Proveedores) y una zona de mensajes.
> - css/estilos.css con un diseño sencillo y responsive, que se vea bien en móvil.
> - js/api.js con funciones para llamar a la API (/api/...) que lancen un error con el mensaje del campo "error" cuando
>   la respuesta no sea correcta.
> - js/productos.js: tabla con los productos, formulario para crear y editar (código, nombre, precio, stock y un
>   desplegable de categorías) y botón de borrar con confirmación.
>
> Mientras se cargan los datos muestra "Cargando..." y enseña en la zona de mensajes los errores de validación o de
> servidor. De momento las secciones de Categorías y Proveedores quedan vacías. No uses git.

Resultado: Junie creó index.html, css/estilos.css, js/api.js y js/productos.js. Avisó de que no pudo comprobar el
JavaScript porque Node no está instalado en mi equipo.

Comprobación: tras reconstruir abrí http://localhost:8080 y probé a crear, editar y borrar un producto. Al guardar uno
con precio negativo apareció el mensaje de error.

### 12. Interfaz web: categorías, proveedores y asignación

Prompt:

> Completa la interfaz con el mismo estilo:
> - js/categorias.js: tabla de categorías, formulario para crear y editar (nombre) y botón de borrar con confirmación.
> - js/proveedores.js: tabla de proveedores, formulario para crear y editar (nombre, teléfono y email) y botón de borrar
>   con confirmación.
> - En la tabla de productos añade un botón "Proveedores" que muestre los proveedores del producto como casillas
>   marcables y un botón para guardar la selección (PUT /api/productos/{id}/proveedores).
>
> Mantén el "Cargando..." y los mensajes de error. Cuando se cree o borre una categoría, el desplegable del formulario
> de productos debe actualizarse. No uses git.

Resultado: Junie añadió js/categorias.js, js/proveedores.js y el panel para asignar proveedores a un producto. Volvió a
avisar de que no pudo comprobar el JavaScript.

Comprobación en el navegador: creé y borré una categoría; al intentar borrar "Informática" apareció el error de que
tiene productos; creé y edité un proveedor; y cambié los proveedores de un producto.

### 13. Tests del servicio y del DAO

Revisión previa: los tests que Junie había ido añadiendo solo comprobaban validaciones y creaban el servicio sin DAO,
así que no probaban ni el caso correcto ni el acceso a datos.

Decisión: probar los servicios con Mockito, usando un DAO simulado, y los DAO con una base de datos H2 en memoria en
modo MySQL. H2 permite ejecutar los tests sin tener Docker arrancado. No es idéntica a MySQL, por lo que el
comportamiento real se comprueba además con la aplicación en Docker.

Prompt:

> Mejora los tests con JUnit 5:
> - Añade al pom.xml, con scope test, org.mockito:mockito-junit-jupiter:5.23.0 y com.h2database:h2:2.4.240.
> - Tests de servicio con Mockito (@ExtendWith(MockitoExtension.class), @Mock del DAO y @InjectMocks del servicio) para
>   CategoriaServicio, ProductoServicio y ProveedorServicio: caso correcto de crear y de obtener,
>   NoEncontradoException
>   cuando el id no existe y las validaciones que ya había.
> - Tests de integración de los DAO con H2 en memoria en modo MySQL (jdbc:h2:mem:...;MODE=MySQL): crea
>   src/test/resources/esquema-test.sql con las mismas cuatro tablas y restricciones que db/init.sql adaptadas a H2 y
>   cárgalo antes de cada test. Para pasar el DataSource en los tests añade a cada DAO un constructor que lo reciba,
>   manteniendo el constructor vacío.
> - Los tests de DAO deben cubrir insertar, buscar, listar, actualizar y borrar, y estos errores: nombre de categoría
>   repetido, código de producto repetido, borrar una categoría con productos y buscar un id que no existe.
>
> Ejecuta mvn test y corrige lo que falle. No cambies el comportamiento de la aplicación ni uses git.

Resultado: Junie añadió las dos dependencias de test, un constructor con DataSource en cada DAO, el esquema para H2 y
los tests nuevos.

Comprobación: ejecuté mvn test y pasaron los 32 tests, sin fallos ni errores. Después reconstruí los contenedores y la
aplicación siguió funcionando.

### 14. README

Prompt:

> Crea el README.md del proyecto en español, con redacción sencilla y sin adornos. Debe incluir:
> 1. Descripción breve de la aplicación y tecnologías usadas.
> 2. Diagrama entidad-relación en Mermaid (erDiagram) con las cuatro tablas y sus relaciones.
> 3. Esquema de la arquitectura de contenedores en Mermaid (flowchart): navegador, app (Payara Micro, puerto 8080), db
>    (MySQL, sin puerto publicado), adminer (puerto 8081), la red interna y el volumen db_data.
> 4. Arquitectura por capas con los paquetes del proyecto.
> 5. Puesta en marcha paso a paso: clonar, copiar .env.example a .env, explicar las cuatro variables y ejecutar docker
>    compose up --build -d.
> 6. Puertos y credenciales de prueba (las de .env.example) y cómo entrar en Adminer.
> 7. Manual breve de uso de la interfaz.
> 8. Endpoints de la API con sus códigos de respuesta.
> 9. Cómo ejecutar los tests (mvn test) y qué cubren.
> 10. Persistencia: los datos se guardan en el volumen db_data; docker compose down los conserva y docker compose down
>     -v los borra.
> 11. Decisiones técnicas: el cifrado entre la aplicación y MySQL está desactivado porque van por la red interna de
>     Docker, y los tests de DAO usan H2 en memoria.
>
> No inventes nada que no esté en el proyecto. No uses git.

Resultado: Junie creó el README.md con los diagramas, la puesta en marcha, los puertos, las credenciales de prueba, el
manual de uso, los endpoints, los tests y la persistencia.

Corrección manual: añadí mi nombre y el módulo debajo del título, un aviso de que la aplicación tarda unos 30 segundos
en estar disponible y una sección final que enlaza con este archivo.

### 15. Prueba de persistencia

Creé la categoría "Prueba persistencia", ejecuté docker compose down y después docker compose up -d. Al listar las
categorías seguía apareciendo, así que los datos se conservan en el volumen db_data aunque se borren los contenedores.