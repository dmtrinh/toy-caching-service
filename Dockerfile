FROM bitnami/java:17

LABEL org.opencontainers.image.source=https://github.com/dmtrinh/toy-caching-service \
      org.opencontainers.image.vendor="The Dark Side" \
      org.opencontainers.image.title="toy-caching-service" \
      org.opencontainers.image.description="Simple service to support implementation of operations that need to be idempotent"

EXPOSE 8080

WORKDIR /app

COPY /build/libs/toy-caching-service-0.0.1-SNAPSHOT.jar /app/toy-caching-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "./toy-caching-service-0.0.1-SNAPSHOT.jar"]
