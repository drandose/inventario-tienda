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
inventario-tienda-1.0-SNAPSHOT.war en la carpeta target.### 2. Crear el .gitignore

Prompt:

> Crea un archivo .gitignore en la raíz del proyecto que ignore target/, .idea/, los archivos *.iml, el archivo .env y
> .DS_Store. No uses git.

Resultado: Junie creó el .gitignore con las cinco reglas pedidas. No hizo falta corregir nada.### 3. Crear el script de
la base de datos

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
falta corregir nada.### 4. Docker Compose con la base de datos

Prompt:

> Crea un docker-compose.yml con dos servicios. El servicio de la aplicación todavía no.
> - db: imagen mysql:8.4. Las variables MYSQL_ROOT_PASSWORD, MYSQL_DATABASE, MYSQL_USER y MYSQL_PASSWORD se leen del
>   archivo .env con los nombres DB_ROOT_PASSWORD, DB_NAME, DB_USER y DB_PASSWORD. Usa un volumen con nombre db_data en
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