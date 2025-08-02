FROM gradle:8.5-jdk24-alpine as builder
COPY ./ ./
RUN gradle clean bootJar

FROM eclipse-temurin:24-jre-alpine 

WORKDIR /usr/src/app

COPY --from=builder /home/gradle/build/libs/*.jar application.jar

EXPOSE 8080

ENTRYPOINT java -jar "application.jar"