FROM gradle:8.1.1-jdk17 AS builder
WORKDIR /

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew bootjar

FROM azul/zulu-openjdk:17
COPY --from=builder build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]

