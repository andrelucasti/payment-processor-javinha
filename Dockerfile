FROM gradle:jdk24-graal as builder
COPY ./ ./
RUN gradle nativeCompile

FROM ghcr.io/graalvm/graalvm-ce:24.0.1-java24

WORKDIR /usr/src/app

COPY --from=builder /home/gradle/build/native/nativeCompile/rinha application

EXPOSE 8080

ENTRYPOINT ["/usr/src/app/application"]