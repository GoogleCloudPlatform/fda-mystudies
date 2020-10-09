# FDA MyStudies Study Builder

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

1. Java 8 or later

    Tomcat 9 requires Java 8 or later to be installed on the server so that any Java web application code can be executed. Below URL covers basic installation of the Java Development Kit package.
        
    [Java Install](https://www.oracle.com/ca-en/java/technologies/javase-downloads.html)

2. Tomcat 9

    Tomcat is an open source implementation of the Java Servlet and Java Server Pages technologies, released by the Apache Software Foundation. Below URL covers the basic installation and some configuration of Tomcat 8 on your Ubuntu server.
    
    [Tomcat Install](https://tomcat.apache.org/download-90.cgi)

3. MySQL 5.7
 
    Below URL covers basic installation of MySQL server and securing MySQL
    
    [MySQL Install](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/)
4. Maven 

    Maven is open source build life cycle management tool by Apache Software Foundation. This tool is required to generate server deployable build (war) from the project. Below URL covers the basic installation and some configuration of Tomcat 8 on your Ubuntu server.
    
    [Maven Install](https://maven.apache.org/index.html)

### Installing

#### Project settings configuration
The `messageResource.properties` file can be find at `wcp/fdahpStudyDesigner/src/main/resources/messageResource.properties` path where following configuration can be customized. 

```properties
max.login.attempts=3                        
#Maximum continuous fail login attempts by a user.

password.resetLink.expiration.in.hour=48    
#Reset password link will get expired after the specified hours.

password.expiration.in.day=90               
#User password expiration in days.

lastlogin.expiration.in.day=90              
#User will get locked if he has not logged in for specified days.

password.history.count=10                   
#User cannot reuse the last 10 generated passwords for change password.

user.lock.duration.in.minutes=30            
#User lock duration in minutes after crossed Maximum continuous fail login attempts limit.
```
#### Changing common configuration 
Override these values in `WCP/fdahpStudyDesigner/src/main/resources/application_local.properties` with following properties settings

```properties
smtp.portvalue=25               
#Should be changed to actual SMTP port

smtp.hostname=xxx.xxx.xxx.xx    
#Should be changed to actual SMTP IP

fda.imgUploadPath=<Tomcat installed path>/webapps/study-resources/     
#<Tomcat installed path> will be changed to actual path

acceptLinkMail =http://localhost:8080/fdahpStudyDesigner/createPassword.do?securityToken=
login.url=http://localhost:8080/fdahpStudyDesigner/login.do
signUp.url=http://localhost:8080/fdahpStudyDesigner/signUp.do?securityToken=
#For all the above properties “localhost” will be changed to actual IP address or domain name.

db.url=localhost/fda_hphc
db.username=****
db.password=****
#“localhost” will be changed to IP address or domain name, if database is installed on different server. If database is installed on same server, it’s not required to change “db.url”.
#“db.username” value will be changed to actual username of database.
#“db.password” value will be changed to actual password of database.

hibernate.connection.url=jdbc:mysql://localhost/fda_hphc
hibernate.connection.username=****
hibernate.connection.password=****

#“localhost” will be changed to IP address or domain name, if database is installed on different server. If database is on same server, it’s not required to change “hibernate.connection.url”.
#“hibernate.connection.username” value will be changed to actual username of database.
#“hibernate.connection.password” value will be changed to actual password of database.

fda.smd.study.thumbnailPath = http://localhost:8080/study-resources/logos/
fda.smd.study.pagePath = http://localhost:8080/study-resources/pages/
fda.smd.resource.pdfPath = http://localhost:8080/study-resources/resources/
fda.smd.questionnaire.image=http://localhost/study-resources/questionnaires/
#For all the properties “localhost” will be changed to actual IP address or domain name.


#Folder for Audit log files:
#Please create a folder "fdaAuditLogs" inside the server and replace the path "/usr/local/fdaAuditLogs/" with actual path for “fda.logFilePath” property.
#User registration server root URL:

fda.registration.root.url = https://hphci-fdama-te-ur-01.labkey.com/fdahpUserRegWS
#https://hphci-fdama-te-ur-01.labkey.com – Should be replaced with actual URL
```
### Database script execution
`WCP/sqlscript/HPHC_My_Studies_DB_Create_Script.sql` file script should be executed in mysql database.

### Build

To build the application the following command should run in project root folder.
```
mvn clean install
```
This command generate a deployable war file in `target` folder named as `fdahpStudyDesigner.war`.

### Deployment

   [Detailed instructions on War file deployment](https://www.baeldung.com/tomcat-deploy-war)

Easiest way is to copy `fdahpStudyDesigner.war` file to the /webapps directory of where Tomcat is installed locally. Restart the Tomcat.

You can also just run `mvn tomcat7:deploy`


### Test application
After complete your deployment, to verify the application 
Hit the below URL, you should see the landing page of the application for WCP application 
`http://localhost:8080/fdahpStudyDesigner`


## Built With

* [Spring](http://spring.io/) - The web framework used
* [Hibernate](http://hibernate.org/) - The ORM used.
* [Maven](https://maven.apache.org/) - Dependency Management
