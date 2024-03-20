FROM maven:3.8.3-openjdk-17 AS maven_builder
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app/
RUN mvn package

FROM tomcat:9.0-jdk17-openjdk
# Note that the filename at dest is different from src.
COPY --from=maven_builder /app/target/study-datastore.war /usr/local/tomcat/webapps/study-datastore.war
CMD ["catalina.sh","run"]
