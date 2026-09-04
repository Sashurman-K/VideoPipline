
FROM bellsoft/liberica-openjdk-alpine:25


RUN apk add --no--cache ffmpeg
WORKDIR /app


COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]