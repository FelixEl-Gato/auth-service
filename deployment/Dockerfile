FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Copia el JAR generado por Gradle desde el módulo de aplicación
ARG JAR_FILE=applications/app-service/build/libs/*.jar
COPY ${JAR_FILE} auth-service.jar

ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar auth-service.jar" ]