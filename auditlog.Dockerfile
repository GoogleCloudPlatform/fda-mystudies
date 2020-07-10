FROM maven:3.6.3-jdk-11-openj9 AS maven_builder
COPY audit-log-module /app/audit-log-module/
COPY common-modules /app/common-modules/
WORKDIR /app/audit-log-module/
RUN mvn clean package

#Torun
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/audit-log-module/audit-log-service/target/audit-log-service.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/audit-log-service.war
CMD ["catalina.sh", "run"]
