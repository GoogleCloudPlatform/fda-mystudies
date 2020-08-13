FROM mohankgonthina/commonmodule:latest AS maven_builder
COPY audit-log-service /app/audit-log-module/audit-log-service/
COPY pom.xml /app/audit-log-module/
WORKDIR /app/audit-log-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true

#Torun
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/audit-log-module/audit-log-service/target/audit-log-service.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/audit-log-service.war
CMD ["catalina.sh", "run"]
