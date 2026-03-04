FROM maven:3.9.9-eclipse-temurin-23 AS build

COPY pom.xml .
COPY src ./src
RUN mvn clean package assembly:single

FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=build /target/GeneratorJSON-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

COPY config/ ./config/

RUN mkdir -p /app/logs

ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV KAFKA_TOPIC=user-transactions

ENTRYPOINT ["java", "-jar", "app.jar"]