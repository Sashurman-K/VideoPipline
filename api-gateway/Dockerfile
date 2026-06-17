FROM bellsoft/liberica-openjdk-alpine:25

WORKDIR /app


COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]