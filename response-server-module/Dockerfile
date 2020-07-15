FROM mohankgonthina/commonmodule:latest AS maven_builder
COPY response-server-service /app/response-server-module/response-server-service/
COPY pom.xml /app/response-server-module/
WORKDIR /app/response-server-module/
RUN mvn clean package

From tomcat:9.0.33-jdk11-openjdk
#RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=maven_builder /app/response-server-module/response-server-service/target/mystudies-response-server.war /usr/local/tomcat/webapps/
#RUN chmod 755 /usr/local/tomcat/webapps/mystudies-response-server.war
CMD ["catalina.sh","run"]
