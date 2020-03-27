FROM maven:3.6.3-jdk-11-openj9 AS maven_builder
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app/
RUN mvn package

FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/target/AuthServer.war /usr/local/tomcat/webapps/
CMD ["catalina.sh","run"]
