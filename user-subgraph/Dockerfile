# Multi-stage build — compile with Maven, run with a slim JRE image.
# Build context must be the repository root so Maven can resolve the parent POM.
#   docker build -f user-subgraph/Dockerfile -t user-subgraph .

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy POMs first so this layer is cached until a POM changes
COPY pom.xml .
COPY user-subgraph/pom.xml user-subgraph/pom.xml
COPY product-subgraph/pom.xml product-subgraph/pom.xml
COPY review-subgraph/pom.xml review-subgraph/pom.xml

# Warm the Maven local cache (layer is reused when POMs haven't changed)
RUN mvn -pl user-subgraph dependency:resolve dependency:resolve-plugins -q

# Copy source and build
COPY user-subgraph/src user-subgraph/src

RUN mvn -pl user-subgraph package -Dmaven.test.skip=true -q

# ── Runtime ───────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/user-subgraph/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
