# Multi-stage build — compile with Maven, run with a slim JRE image.
# Build context must be the repository root so Maven can resolve the parent POM.
#   docker build -f product-subgraph/Dockerfile -t product-subgraph .

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy POMs first so this layer is cached until a POM changes
COPY pom.xml .
COPY user-subgraph/pom.xml user-subgraph/pom.xml
COPY product-subgraph/pom.xml product-subgraph/pom.xml
COPY review-subgraph/pom.xml review-subgraph/pom.xml

# Warm the Maven local cache (layer is reused when POMs haven't changed)
RUN mvn -pl product-subgraph dependency:resolve dependency:resolve-plugins -q

# Copy source and build
COPY product-subgraph/src product-subgraph/src

RUN mvn -pl product-subgraph package -Dmaven.test.skip=true -q

# ── Runtime ───────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/product-subgraph/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
