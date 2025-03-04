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

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=${PROFILES}
ENV SERVER_ENV=$ENV
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_ISSUER=${JWT_ISSUER}
ENV JWT_SECRET=${JWT_SECRET}
ENV DEPLOY_SECRET_TOKEN=${DEPLOY_SECRET_TOKEN}

COPY build/libs/*.jar app.jar

# Run the application with environment variables
ENTRYPOINT ["java", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-Dserver.env=${SERVER_ENV}", \
    "-Dspring.datasource.url=${DB_URL}", \
    "-Dspring.datasource.username=${DB_USERNAME}", \
    "-Dspring.datasource.password=${DB_PASSWORD}", \
    "-Djwt.issuer=${JWT_ISSUER}", \
    "-Djwt.secret=${JWT_SECRET}", \
    "-Ddeploy.secret.token=${DEPLOY_SECRET_TOKEN}", \
    "-jar", "app.jar"]
