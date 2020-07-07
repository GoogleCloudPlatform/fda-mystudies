FROM maven:3.6.3-jdk-11-openj9 AS maven_builder
COPY consent-mgmt /app/consent-mgmt/
COPY enroll-mgmt /app/enroll-mgmt/
COPY user-mgmt /app/user-mgmt/
WORKDIR /app/consent-mgmt/
RUN mvn package
WORKDIR /app/enroll-mgmt/
RUN mvn package
WORKDIR /app/user-mgmt/
RUN mvn package

FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/consent-mgmt/target/myStudiesConsentMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesConsentMgmtWS.war
COPY --from=maven_builder /app/enroll-mgmt/target/myStudiesEnrollmentMgmt.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesEnrollmentMgmt.war
COPY --from=maven_builder /app/user-mgmt/target/myStudiesUserMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesUserMgmtWS.war
CMD ["catalina.sh", "run"]
