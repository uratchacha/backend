FROM openjdk:17-jdk-slim

# Build arguments
ARG PROFILES
ARG ENV
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG JWT_ISSUER
ARG JWT_SECRET
ARG DEPLOY_SECRET_TOKEN

ARG FIREBASE_CONFIG

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=${PROFILES}
ENV ENV=${ENV}
ENV REDIS_PASSWORD=${REDIS_PASSWORD}
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_ISSUER=${JWT_ISSUER}
ENV JWT_SECRET=${JWT_SECRET}
ENV DEPLOY_SECRET_TOKEN=${DEPLOY_SECRET_TOKEN}

COPY src/main/resources/firebase-service-account.json /app/src/main/resources/firebase-service-account.json
COPY build/libs/*.jar app.jar

# Run the application with environment variables
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=${PROFILES}", \
    "-Dserver.env=${ENV}", \
    "-Dspring.datasource.url=${DB_URL}", \
    "-Dspring.datasource.username=${DB_USERNAME}", \
    "-Dspring.datasource.password=${DB_PASSWORD}", \
    "-Dspring.data.redis.password=${REDIS_PASSWORD}", \

    "-Djwt.issuer=${JWT_ISSUER}", \
    "-Djwt.secret=${JWT_SECRET}", \
    "-Ddeploy.secret.token=${DEPLOY_SECRET_TOKEN}", \
    "-jar", "app.jar"]
