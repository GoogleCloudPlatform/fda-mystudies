FROM mohankgonthina/commonmodule:latest AS maven_builder
COPY consent-mgmt-module /app/consent-mgmt-module/
COPY enroll-mgmt-module /app/enroll-mgmt-module/
COPY user-mgmt-module /app/user-mgmt-module/
WORKDIR /app/consent-mgmt-module/
RUN mvn clean package
WORKDIR /app/enroll-mgmt-module/
RUN mvn clean package
WORKDIR /app/user-mgmt-module/
RUN mvn clean package

#Torun
FROM tomcat:9.0.33-jdk11-openjdk
COPY --from=maven_builder /app/consent-mgmt-module/consent-mgmt/target/myStudiesConsentMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesConsentMgmtWS.war
COPY --from=maven_builder /app/enroll-mgmt-module/enroll-mgmt/target/myStudiesEnrollmentMgmt.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesEnrollmentMgmt.war
COPY --from=maven_builder /app/user-mgmt-module/user-mgmt/target/myStudiesUserMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesUserMgmtWS.war
CMD ["catalina.sh", "run"]
