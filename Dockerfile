FROM openjdk:17-jdk-slim

ARG PROFILES
ARG ENV

ENV SPRING_PROFILES_ACTIVE=${PROFILES}
ENV SERVER_ENV=${ENV}

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-Dserver.env=${SERVER_ENV}", "-jar", "app.jar"]