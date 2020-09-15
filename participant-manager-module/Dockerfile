# first build common module, then run:
# export DOCKER_ID=<YourDockerID>
# docker image build --tag $DOCKER_ID/participant-manager-module . --build-arg DOCKER_ID
ARG DOCKER_ID
FROM ${DOCKER_ID}/commonmodule:latest AS maven_builder
COPY participant-manager-service /app/participant-manager-module/participant-manager-service
COPY pom.xml /app/participant-manager-module/
WORKDIR /app/participant-manager-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true

# to run
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/participant-manager-module/participant-manager-service/target/participant-manager-service.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/participant-manager-service.war
CMD ["catalina.sh", "run"]
