# ----------------------------
# STAGE 1: Build with Maven
# ----------------------------
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml and Maven wrapper to leverage Docker cache
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make wrapper executable and download dependencies (offline)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build the JAR
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ----------------------------
# STAGE 2: Runtime image
# ----------------------------
FROM eclipse-temurin:17-jre

# Set timezone and allow additional JVM options via ENV
ENV TZ=UTC \
    JAVA_OPTS=""

WORKDIR /app

# Copy the JAR produced in the build stage
COPY --from=build /app/target/accounts-import-service-*.jar app.jar

# Expose the service port (as defined in application.properties)
EXPOSE 8083

# Entrypoint to run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]