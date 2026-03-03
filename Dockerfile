# Используем официальный образ Java
FROM openjdk:23-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл (собранный через Maven)
COPY target/GeneratorJSON-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Копируем конфигурационные файлы (если есть)
COPY config/ /app/config/

# Создаем директорию для логов
RUN mkdir -p /app/logs

# Указываем переменные окружения для Kafka
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV KAFKA_TOPIC=user-transactions

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM ubuntu:latest
#LABEL authors="semka"
#
#ENTRYPOINT ["top", "-b"]