# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
# Build a Spring Boot executable jar and copy ONLY the repackaged jar to /app/app.jar
RUN mvn -q -DskipTests clean package spring-boot:repackage && \
    JAR=$(ls -1 target/*.jar | grep -v original) && \
    cp "$JAR" /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["bash","-lc","java $JAVA_OPTS -jar /app/app.jar"]
