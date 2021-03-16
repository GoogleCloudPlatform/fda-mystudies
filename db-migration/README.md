<!--
 Copyright 2021 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

## Migrating Databases

### Introduction
This guide provides instructions for migrating databases between versions of
FDA MyStudies using the [Flyway Maven plugin](https://flywaydb.org/documentation/usage/maven/).
This can be used to migrate between compatible versions of FDA MyStudies.

### Before you begin
Confirm the user account you will use has the `Project Owner` role in the
following projects:

*  `{PREFIX}-{ENV}-data`
*  `{PREFIX}-{ENV}-networks`
*  `{PREFIX}-{ENV}-secrets`

If you followed the [Deployment Guide](https://github.com/GoogleCloudPlatform/fda-mystudies/blob/master/deployment/README.md),
you can use a user which you added to the group `{PREFIX}-{ENV}-project-owners@{DOMAIN}`

### Connect to virtual machine

This process utilizes connecting to the `bastion-vm` in the `{PREFIX}-{ENV}-networks`
to execute the commands.

1.  Within Cloud Console, go to the [Compute Engine Instances](http://console.cloud.google.com/compute/instances)
    page and select the project `{PREFIX}-{ENV}-networks` at the top of the page.
1.  Connect to the instance with SSH using the SSH button next to the instance
    named `bastion-vm`.

### Install or confirm prior installation of dependencies

1.  Install OpenJDK

    ```
    wget https://download.java.net/java/GA/jdk13.0.1/cec27d702aa74d5a8630c65ae61e4305/9/GPL/openjdk-13.0.1_linux-x64_bin.tar.gz
    tar -xvf openjdk-13.0.1_linux-x64_bin.tar.gz
    mv jdk-13.0.1 /opt/
    ```

1. Install Maven

   ```
   wget https://mirrors.estointernet.in/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz 
   tar -xvf apache-maven-3.6.3-bin.tar.gz 
   mv apache-maven-3.6.3 /opt/
   ```

1. Add Java and Maven to `PATH`

   Add the following lines at the end of the user profile file `~/.profile` using
   a text editor 

   ```
   M2_HOME='/opt/apache-maven-3.6.3'
   JAVA_HOME='/opt/jdk-13.0.1' 
   PATH="$M2_HOME/bin:$JAVA_HOME/bin:$PATH" 
   export PATH
   ```

1. Relaunch terminal

   After editing the `~/.profile` file and saving, exit and relaunch the SSH
   connection to apply the configuration changes.

### Set up Cloud SQL Proxy

Set up [Cloud SQL Proxy](https://cloud.google.com/sql/docs/mysql/sql-proxy) to
be able to connect to the Cloud SQL instance using the private IP within the
VPC without the need for Authorized networks or for configuring SSL.

1. Install Cloud SQL Proxy

   ```
   wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
   chmod +x cloud_sql_proxy
   ```

1. Authenticate user

   Login as a user with the `Project Owner` role as described above and update
   your application default credentials. (When using a Google Compute Engine VM
   such as `bastion-vm` you must update the application default credentials,
   otherwise requests will continue to be made with its default service account).
   Remember to log your user account out once your deployment is complete.

   ```
   gcloud auth login --update-adc
   ```

1. Authenticate the Cloud SQL Proxy

   Authenticate the Cloud SQL Proxy, replacing the variables below with the
   specifics for your deployment. If you are unsure of the region, you can
   check by going to the [Cloud SQL Instances](https://console.cloud.google.com/sql/instances)
   page in Cloud Console and selecting the project `{PREFIX}-{ENV}-data` at the
   top of the page. You will see the full path in the column:
   `Instance connection name`

   ```
   ./cloud_sql_proxy -instances=<{PREFIX}-{ENV}-data:{REGION}:mystudies>=tcp:3306
   ```

### Data Migration Script Execution Setup:

1. Open another SSH connection terminal to `bastion-vm` through SSH and  perform
   the user authentication and application default credentials as above, for
   example:

   ```
   gcloud auth login --update-adc
   ```

1. Clone your repository

   Clone the repository and branch with the new version of FDA MyStudies. You
   will need to authenticate your Git user if you are accessing a private
   repository. In the following command, replace the variables with the
   specifics for your deployment, for example:

   ```
   git clone -b <<REPO_BRANCH>> https://github.com/<REPO_OWNER>/<REPO>
   ```

1. Set environment variables

   Edit `<REPO_PATH>/deployment/scripts/set_env_var.sh` to set the variables for
   your deployment using a text editor. Then run this script to set your
   environment variables, for example:

   ```
   source set_env_var.sh    # executed from your /deployment/scripts directory
   ```

1. Set database credentials

   Save the database credentials to environment variables for the migration.
   You can either find these values in Cloud Console [Secret Manager](https://console.cloud.google.com/security/secret-manager)
   in the `{PREFIX}-{ENV}-secrets` project, or retrieve them with `gcloud`, for
   example:

   ```
   gcloud config set project $PREFIX-$ENV-secrets
   export DB_USER=$( \
   gcloud secrets versions access latest --secret="auto-auth-server-db-user")
   export DB_PASS=$( \
   gcloud secrets versions access latest --secret="auto-auth-server-db-password")
   ```

### Run Data Migration Scripts

To migrate the data, run each of the following for each of the databases
requiring migration.

#### Auth Server Database
```
cd $GIT_ROOT/db-migration/auth-server-db-migration/
mvn clean flyway:migrate -Dflyway.user=${DB_USER} -Dflyway.configFiles=flyway.properties -Dflyway.password=${DB_PASS} -Dflyway.url=jdbc:mysql://127.0.0.1:3306/<Auth-server database name>
```

#### Participant Datastore Database
```
cd $GIT_ROOT/db-migration/participant-datastore-db-migration/
mvn clean flyway:migrate -Dflyway.user=${DB_USER} -Dflyway.configFiles=flyway.properties -Dflyway.password=${DB_PASS} -Dflyway.url=jdbc:mysql://127.0.0.1:3306/<participant-datastore database name>
```

#### Response Datastore database
```
cd $GIT_ROOT/db-migration/response-datastore-db-migration/
mvn clean flyway:migrate -Dflyway.user=${DB_USER} -Dflyway.configFiles=flyway.properties -Dflyway.password=${DB_PASS} -Dflyway.url=jdbc:mysql://127.0.0.1:3306/<response-datastore database name>
```

#### Study Builderdatabase
```
cd $GIT_ROOT/db-migration/study-builder-db-migration/
mvn clean flyway:migrate -Dflyway.user=${DB_USER} -Dflyway.configFiles=flyway.properties -Dflyway.password=${DB_PASS} -Dflyway.url=jdbc:mysql://127.0.0.1:3306/<study-builder database name>
```
