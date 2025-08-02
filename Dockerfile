FROM gradle:jdk24-ubi-minimal as builder
COPY ./ ./
RUN gradle clean bootJar

FROM eclipse-temurin:24.0.1_9-jre-ubi9-minimal

WORKDIR /usr/src/app

COPY --from=builder /home/gradle/build/libs/*.jar application.jar

EXPOSE 8080

ENTRYPOINT java -jar "application.jar"