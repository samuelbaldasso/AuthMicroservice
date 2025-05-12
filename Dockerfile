# Build stage
FROM maven:3.8.6-eclipse-temurin-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=builder /app/target/mybank-0.0.1-SNAPSHOT.jar mybank.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "mybank.jar"]