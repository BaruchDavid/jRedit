FROM openjdk:11.0.8 AS build
WORKDIR skylink
RUN apt-get update -y
RUN apt-get upgrade -y
RUN apt-get install -y apt-utils    --no-install-recommends
RUN apt-get install -y libfreetype6 --no-install-recommends
RUN apt-get install -y fontconfig   --no-install-recommends
RUN apt-get install -y fonts-dejavu --no-install-recommends
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11.0.8

WORKDIR skylink
COPY --from=build skylink/dependencies/ ./
COPY --from=build skylink/spring-boot-loader/ ./
COPY --from=build skylink/snapshot-dependencies/ ./
COPY --from=build skylink/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]