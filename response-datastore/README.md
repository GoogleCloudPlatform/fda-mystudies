<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
The **FDA MyStudies** [`Response datastore`](/response-datastore/) provides APIs to store and manage pseudonymized study response and activity data. The `Response datastore` receives de-identified study responses from the [`Android`](/Android) and [`iOS`](/iOS) mobile applications, which are then written to a document-oriented database. Researchers and data scientists access this database to perform analysis of the study data, or for export to downstream systems. The `Response datastore` also receives de-identified activity data, which is written to a relational database. The application is built as a Spring Boot application. The backend database is Cloud Firestore for the response data, and MySQL for the activity data. The `Response datastore` uses basic authentication `client_id` and `client_secret`.

The `Response datastore` client applications are:
1. [`Android mobile application`](/Android/)
1. [`iOS mobile application`](/iOS/)
1. [`Study builder`](/study-builder)
1. [`Participant datastore`](/participant-datastore/)
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
To deploy the [`Response datastore`](/response-datastore/) manually:
1. [Create](https://cloud.google.com/compute/docs/instances/create-start-instance) a Compute Engine VM instance with a [static IP](https://cloud.google.com/compute/docs/ip-addresses/reserve-static-internal-ip-address) and read/write [access scopes](https://cloud.google.com/compute/docs/access/service-accounts#accesscopesiam) for Cloud Datastore
1. Verify that your VM instance has the `Stackdriver Logging API` write [access scope](https://cloud.google.com/compute/docs/access/service-accounts#accesscopesiam) (on by default) and that your VM’s [service account](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) has the [`Logs Writer`](https://cloud.google.com/logging/docs/access-control) role (off by default)
1. Check out the latest code from the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies/)
1. Create a Cloud SQL instance with MySQL v5.7 ([instructions](https://cloud.google.com/sql/docs/mysql/create-instance))
1. Configure the `Response datastore` database on the Cloud SQL instance
    -    Create a user account that the `Response datastore` application will use to access this instance ([instructions](https://cloud.google.com/sql/docs/mysql/create-manage-users))
    -    Run the [`mystudies_response_server_db_script.sql`](sqlscript/mystudies_response_server_db_script.sql) script to create a database named `mystudies_response_server` ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file))
    -   Enable the database’s private IP connectivity in the same network as your VM ([instructions](https://cloud.google.com/sql/docs/mysql/configure-private-ip))
1. Create a Cloud Firestore database operating in [*Native mode*](https://cloud.google.com/firestore/docs/quickstart-servers), then grant the IAM role [`roles/datastore.user`](https://cloud.google.com/datastore/docs/access/iam#iam_roles) to the service account that your `Response datastore` will use to read/write data (this could be your VM’s [default service](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) account)
1. Deploy the `Response datastore` container to the VM
    -    Create the Docker image using `sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -Dimage=response-datastore-image` from the `response-datastore/` directory (you may need to [install Docker](https://docs.docker.com/engine/install/debian/) and Maven, for example `sudo apt install maven`)
    -    Update the Docker environment file [`variables.env`](variables.env) with values that configure the [`application.properties`](response-server-service/src/main/resources/application.properties) file for your deployment
    -    Run the container on the VM using `sudo docker run --detach --env-file variables.env -p 80:8080 --name response-datastore response-datastore-image`
    -    If your `Hydra` instance is a using self-signed certificate, add that certificate to your container’s keystore, for example with `sudo docker exec -it response-datastore bash -c "openssl s_client -connect <your_hydra_instance> | sed -ne '/-BEGIN CERTIFICATE/,/END CERTIFICATE/p' > hydra.crt; keytool -import -trustcacerts -alias hydra -file hydra.crt -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit"`, then restart the container with `sudo docker restart response-datastore`
1. Test if the application is running with `curl http://0.0.0.0/response-datastore/healthCheck`
1. You can review application logs in the logging directories you specified, or with `sudo docker logs response-datastore`; audit logs are available in [Cloud Logging](https://cloud.google.com/logging)

***
<p align="center">Copyright 2020 Google LLC</p>
