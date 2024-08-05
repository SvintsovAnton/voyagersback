FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn -DskipTests=true clean package

RUN mkdir -p target/dependecy &&(cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine

ARG DEPENDENCY=/workspace/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF  /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT["java","-cp","app:app/lib/*","group9.events.EventsApplication"]
