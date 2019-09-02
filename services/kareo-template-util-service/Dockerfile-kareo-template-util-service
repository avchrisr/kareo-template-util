#FROM openjdk:8-jdk-alpine
# alpine versions do not exist for openjdk:11+ and the base images are so large..
#FROM openjdk:11-jdk
FROM adoptopenjdk/openjdk11:latest
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
