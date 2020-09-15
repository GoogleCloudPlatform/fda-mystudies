# first build common module, then run:
# export DOCKER_ID=<YourDockerID>
# docker image build --tag $DOCKER_ID/oauth-scim-module . --build-arg DOCKER_ID
ARG DOCKER_ID
FROM ${DOCKER_ID}/commonmodule:latest AS maven_builder
COPY oauth-scim-service /app/oauth-scim-module/oauth-scim-service
COPY pom.xml /app/oauth-scim-module/
WORKDIR /app/oauth-scim-module/
RUN mvn clean package

# to run
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/oauth-scim-module/oauth-scim-service/target/oauth-scim-service.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/oauth-scim-service.war
CMD ["catalina.sh", "run"]
