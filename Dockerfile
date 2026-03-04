FROM maven:3.9.9-eclipse-temurin-23 AS build

COPY pom.xml .
COPY src ./src
RUN mvn clean package assembly:single

# Финальный образ
FROM eclipse-temurin:23-jre

WORKDIR /app

# Копируем собранный JAR
COPY --from=build /target/GeneratorJSON-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Копируем конфигурацию
COPY config/ ./config/

# Создаем директорию для логов
RUN mkdir -p /app/logs

ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV KAFKA_TOPIC=user-transactions

ENTRYPOINT ["java", "-jar", "app.jar"]