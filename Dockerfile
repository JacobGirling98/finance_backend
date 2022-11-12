# syntax=docker/dockerfile:1

FROM eclipse-temurin:11-jdk-jammy

WORKDIR /app

COPY gradle/ gradle
COPY build.gradle.kts build.gradle.kts
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat

RUN ./gradlew clean installDist

COPY build/install/ install

ENTRYPOINT ["./install/finance_backend/bin/finance_backend"]