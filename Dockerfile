FROM gradle:jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11.0.9-jre-slim

RUN mkdir /app
EXPOSE 9000
COPY --from=build /home/gradle/src/build/libs/ /app/

ENTRYPOINT ["java","-jar","/app/finance_backend-1.0-SNAPSHOT.jar"]

# sample command docker run -e DATA_LOC=/app/data -v $DATA_LOC:/app/data -p 9000:9000 -t v1