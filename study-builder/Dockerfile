# Package WCP/fdahpStudyDesigner
FROM maven:3.6.3-jdk-11-openj9 AS maven_builder
COPY fdahpStudyDesigner/pom.xml /app/
COPY fdahpStudyDesigner/src /app/src/
WORKDIR /app/
RUN mvn package
RUN ls /app/target

FROM tomcat:9.0.33-jdk11-openjdk

COPY --from=maven_builder /app/target/studybuilder.war /usr/local/tomcat/webapps/
CMD ["catalina.sh","run"]
