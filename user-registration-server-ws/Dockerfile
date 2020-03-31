#TODO(honkwan): Include building the war files in future.
FROM tomcat:9.0.33-jdk11-openjdk
RUN rm -rf /usr/local/tomcat/webapps/*
COPY consent-mgmt/target/myStudiesConsentMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesConsentMgmtWS.war
COPY enroll-mgmt/target/myStudiesEnrollmentMgmt.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesEnrollmentMgmt.war
COPY user-mgmt/target/myStudiesUserMgmtWS.war /usr/local/tomcat/webapps/
RUN chmod 755 /usr/local/tomcat/webapps/myStudiesUserMgmtWS.war
CMD ["catalina.sh", "run"]