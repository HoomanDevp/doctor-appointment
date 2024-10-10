FROM eclipse-temurin:17-jre-alpine as builder

WORKDIR /app

COPY config/application-dev.properties /app/config/
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${@} -jar ./app.jar"]
