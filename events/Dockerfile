FROM openjdk:17-jdk as build

WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn -DskipTest=true clean package

RUN mkdir -p target/dependency &&(cd target/dependency; jar -xf ../*.jar)
FROM eclipse-temurin:17-jre-alpine


ARG DEPENDENCY=/workspace/apptarget/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF/lib /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*","group9.events.EventsApplication"]