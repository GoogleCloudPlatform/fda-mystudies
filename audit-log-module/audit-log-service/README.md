Project Dependencies:
=============================================
This project has a dependency on common-tests and common-service modules. 
Run 'mvn clean install' command from common-tests and common-service modules to resolve any compilation issues in this project.

Test Coverage Report
=============================================
Below command generates test reports at /target/site/clover. Open the index.html in a browser to see the coverage details.

mvn clean clover:setup test clover:aggregate clover:clover


Package the application for tomcat server
=============================================
Below command runs the integration tests with profile 'mockit' and creates a war file under /target

mvn clean package -Pprofile_name

replace 'profile_name' with dev, qa or test. Default profile is 'local'


Start the application with embedded server
=============================================
#1. Below command runs integration tests and creates a war package under /target

mvn clean package spring-boot:repackage

#2. set the active profile name and start the application on different port

java -jar -Dspring.profiles.active=local -Dserver.port=8002 target/audit-log-service.war

Postman collection
=============================================
Import the audit-log-service.postman_collection.json into postman to test /audit-log-service/v1/health  and /audit-log-service/v1/events endpoints.


Known Issues:
=============================================

1) Running tests from STS throws java.lang.NoClassDefFoundError: Lcom_atlassian_clover/TestNameSniffer;

Solution: From STS/Eclipse IDE, select Project -> Clean -> Select the project to rebuild -> Click OK