FROM gradle:jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM arm64v8/amazoncorretto:11.0.17

RUN mkdir /app
EXPOSE 9000
COPY --from=build /home/gradle/src/build/libs/ /app/
COPY --from=build /home/gradle/src/build/resources/main/properties/docker.yaml /app/

ENTRYPOINT ["java","-jar","/app/finance_backend-1.0-all.jar"]

# sample command docker run -e DATA_LOC=/app/data -v $DATA_LOC:/app/data -p 9000:9000 -t v1