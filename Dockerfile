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

COPY build/libs/*.jar app.jar

# Run the application with environment variables
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=${PROFILES}", \
    "-Dserver.env=${ENV}", \
    "-Dspring.datasource.url=${DB_URL}", \
    "-Dspring.datasource.username=${DB_USERNAME}", \
    "-Dspring.datasource.password=${DB_PASSWORD}", \
    "-Djwt.issuer=${JWT_ISSUER}", \
    "-Djwt.secret=${JWT_SECRET}", \
    "-Ddeploy.secret.token=${DEPLOY_SECRET_TOKEN}", \
    "-jar", "app.jar"]
