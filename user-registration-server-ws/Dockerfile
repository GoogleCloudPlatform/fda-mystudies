# first build common module, then run:
# export DOCKER_ID=<YourDockerID>
# docker image build --tag $DOCKER_ID/user-registration-server . --build-arg DOCKER_ID
ARG DOCKER_ID
FROM ${DOCKER_ID}/commonmodule:latest AS maven_builder
COPY consent-mgmt-module /app/user-registration-server/consent-mgmt-module/
COPY enroll-mgmt-module /app/user-registration-server/enroll-mgmt-module/
COPY user-mgmt-module /app/user-registration-server/user-mgmt-module/
WORKDIR /app/user-registration-server/consent-mgmt-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true
WORKDIR /app/user-registration-server/enroll-mgmt-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true
WORKDIR /app/user-registration-server/user-mgmt-module/
RUN mvn clean package -Pqa -Dmaven.test.skip=true

# to run
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/user-registration-server/consent-mgmt-module/consent-mgmt/target/myStudiesConsentMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesConsentMgmtWS.war
COPY --from=maven_builder /app/user-registration-server/enroll-mgmt-module/enroll-mgmt/target/myStudiesEnrollmentMgmt.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesEnrollmentMgmt.war
COPY --from=maven_builder /app/user-registration-server/user-mgmt-module/user-mgmt/target/myStudiesUserMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesUserMgmtWS.war
CMD ["catalina.sh", "run"]
