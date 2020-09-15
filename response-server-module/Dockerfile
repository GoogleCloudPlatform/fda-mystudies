# first build common module, then run:
# export DOCKER_ID=<YourDockerID>
# docker image build --tag $DOCKER_ID/response-server-module . --build-arg DOCKER_ID
ARG DOCKER_ID
FROM ${DOCKER_ID}/commonmodule:latest AS maven_builder
COPY response-server-service /app/response-server-module/response-server-service
COPY pom.xml /app/response-server-module/
WORKDIR /app/response-server-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true

# to run
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/response-server-module/response-server-service/target/mystudies-response-server.war /usr/local/tomcat/webapps/
CMD ["catalina.sh","run"]
