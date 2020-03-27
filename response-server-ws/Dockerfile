FROM maven:3.6.3-jdk-11-openj9 AS maven_builder
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app/
RUN mvn package

From tomcat:9.0.33-jdk11-openjdk
#RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=maven_builder /app/target/mystudies-response-server.war /usr/local/tomcat/webapps/
#RUN chmod 755 /usr/local/tomcat/webapps/mystudies-response-server.war
CMD ["catalina.sh","run"]
