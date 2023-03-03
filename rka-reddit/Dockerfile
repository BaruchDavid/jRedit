FROM openjdk:11.0.8 as build
WORKDIR application
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

WORKDIR application
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]