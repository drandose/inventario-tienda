FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM payara/micro:7.2026.11
COPY --from=build /app/target/inventario-tienda-1.0-SNAPSHOT.war /opt/payara/deployments/inventario-tienda.war
CMD ["--deploy", "/opt/payara/deployments/inventario-tienda.war", "--contextroot", "ROOT", "--noCluster"]