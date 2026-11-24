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

Resultado: Junie creó el .gitignore con las cinco reglas pedidas. No hizo falta corregir nada.