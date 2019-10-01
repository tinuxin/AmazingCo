FROM openjdk:12-jdk-alpine

COPY build/libs/amazingco.jar amazingco.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/amazingco.jar"]