<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
The **FDA MyStudies** [`Participant datastore`](/participant-datastore/) is a set of three modules that provide APIs to store and manage participant registration, enrollment and consent. The [`Android`](/Android) and [`iOS`](/iOS) mobile applications interact with the `Participant datastore` to store and retrieve profile information specific to participants, and to establish enrollment and consent. The [`Response datastore`](/response-datastore/) interacts with the `Participant datastore` to determine enrollment state of participants. Pseudonymized study response data is stored in the `Response datastore` without participant identifiers (for example, answers to survey questions and activity data). Identifiable participant data is stored in the `Participant datastore` without study response data (for example, consent forms and participant contact information). This separation is designed to allow the deploying organization to configure distinct access control for each class of data. The `Participant datastore` uses basic authentication `client_id` and `client_secret` that are provided to client applications and managed by [`Hydra`](/hydra/).

The Participant datastore is composed of three applications that share a common database:

Module | Purpose | Client applications | Directory
---------------------|-----------------------------------------|-------------------|------------
`User module` | Maintains participant state and information | [`Study builder`](/study-builder/)<br/>[`Study datastore`](/study-datastore/)<br/>[`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/user-mgmt-module/](/participant-datastore/user-mgmt-module/)
`Enrollment module` | Maintains participant enrollment status  | [`Response datastore`](/response-datastore/)<br/>[`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/enroll-mgmt-module/](/participant-datastore/enroll-mgmt-module/)
`Consent module` | Maintains participant consent version status and provides access to generated consent documents | [`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/consent-mgmt-module/](/participant-datastore/consent-mgmt-module/)
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
To deploy the [`Participant datastore`](/participant-datastore/) manually:
1. [Create](https://cloud.google.com/compute/docs/instances/create-start-instance) a Compute Engine VM instance with a [static IP](https://cloud.google.com/compute/docs/ip-addresses/reserve-static-internal-ip-address) and read/write [access scopes](https://cloud.google.com/compute/docs/access/service-accounts#accesscopesiam) for Cloud Storage
1. Verify that your VM instance has the `Stackdriver Logging API` write [access scope](https://cloud.google.com/compute/docs/access/service-accounts#accesscopesiam) (on by default) and that your VM’s [service account](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) has the [`Logs Writer`](https://cloud.google.com/logging/docs/access-control) role (off by default)
1. Check out the latest code from the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies/)
1. Create a Cloud SQL instance with MySQL v5.7 ([instructions](https://cloud.google.com/sql/docs/mysql/create-instance)) 
1. Configure the `Participant datastore` database on the Cloud SQL instance
    -    [Create](https://cloud.google.com/sql/docs/mysql/create-manage-users) a user account that the `User module`, `Enrollment module` and `Consent module` will use to access this instance
    -   Run `./create_superadmin.sh <email> <password>` in the `sqlscript/` directory to create `pm-superadmin.sql`, which you will use to create the first admin user for `Participant manager`
(you may need to install [htpasswd](https://httpd.apache.org/docs/2.4/programs/htpasswd.html), for example `sudo apt-get install apache2-utils`)
    -    Run the [`mystudies_participant_datastore_db_script.sql`](sqlscript/mystudies_participant_datastore_db_script.sql) script to create a database named `mystudies_participant_datastore` ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file)) 
    -    Run the `pm-superadmin.sql` script that you created in the step above
    -    Enable the database’s private IP connectivity in the same network as your VM ([instructions](https://cloud.google.com/sql/docs/mysql/configure-private-ip))
1. Configure blob storage for participant consent forms by [creating](https://cloud.google.com/storage/docs/creating-buckets) a Google Cloud Storage bucket and granting your VM’s [GCE service account](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) the [`Storage Object Admin`](https://cloud.google.com/storage/docs/access-control/iam-roles) role
1. Configure the Firebase Cloud Messaging API for your deployment  ([documentation](https://firebase.google.com/docs/cloud-messaging/http-server-ref))
1. Deploy the container for each `Participant datastore` module to the VM
    -    Create Docker images for each of the modules (you may need to [install Docker](https://docs.docker.com/engine/install/debian/) and Maven, for example `sudo apt install maven`):
         ```bash
         sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -f user-mgmt-module/pom.xml -Dimage=user-mgmt-image && \
         sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -f enroll-mgmt-module/pom.xml -Dimage=enroll-mgmt-image && \
         sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -f consent-mgmt-module/pom.xml -Dimage=consent-mgmt-image
         ```
    -    Update the Docker environment files [`user-mgmt-module/variables.env`](user-mgmt-module/variables.env), [`enroll-mgmt-module/variables.env`](enroll-mgmt-module/variables.env) and [`consent-mgmt-module/variables.env`](consent-mgmt-module/variables.env) with values that configure the `application.properties` files for your deployment
    -    Run the containers on the VM using:
         ```bash
         sudo docker run --detach --env-file user-mgmt-module/variables.env -p 8080:8080 --name user-mgmt user-mgmt-image && \
         sudo docker run --detach --env-file enroll-mgmt-module/variables.env -p 8081:8080 --name enroll-mgmt enroll-mgmt-image && \
         sudo docker run --detach --env-file consent-mgmt-module/variables.env -p 8082:8080 --name consent-mgmt consent-mgmt-image
         ```
    -    If your `Hydra` instance is a using self-signed certificate, add that certificate to each of the container keystores, for example with `sudo docker exec -it <container_name> bash -c "openssl s_client -connect <your_hydra_instance> | sed -ne '/-BEGIN CERTIFICATE/,/END CERTIFICATE/p' > hydra.crt; keytool -import -trustcacerts -alias hydra -file hydra.crt -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit”`, then restart the containers
1. Test if the applications are running with
     ```bash
    curl http://0.0.0.0:8080/participant-user-datastore/healthCheck
    curl http://0.0.0.0:8081/participant-enroll-datastore/healthCheck
    curl http://0.0.0.0:8082/participant-consent-datastore/healthCheck
    ````
1. You can review application logs in the logging directories you specified, or with `sudo docker logs <container_name>`; audit logs are available in [Cloud Logging](https://cloud.google.com/logging)

***
<p align="center">Copyright 2020 Google LLC</p>
