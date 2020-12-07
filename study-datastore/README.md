<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
The **FDA MyStudies** [`Study datastore`](/study-datastore/) makes APIs available for client applications to obtain the content of studies configured with the [`Study builder`](/study-builder/) web application. For example, the [`iOS`](/iOS/) and [`Android`](/Android/) mobile applications interact with the `Study datastore` to obtain the study schedule and tasks. The `Study datastore` serves the content and configuration of your organization’s studies - it does not process participant data. The `Study datastore` is a Java application built on the Spring framework. The backend database is MySQL, which is shared with the `Study builder` web application. The `Study datastore` uses basic authentication `bundle_id` and `app_token` to authenticate calls from client applications.
 
The `Study datastore` client applications are:
1. [`Android mobile application`](/Android/)
1. [`iOS mobile application`](/iOS/)
1. [`Response datastore`](/response-datastore/)
 
The `Study datastore` provides the following functionality:
1. Serve study settings to client applications
1. Serve study eligibility and consent data to client applications
1. Serve study schedule to client applications
1. Serve study activities to client applications
1. Serve study status to client applications
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
To deploy the [`Study datastore`](/study-datastore/) manually:
1. [Create](https://cloud.google.com/compute/docs/instances/create-start-instance) a Compute Engine VM instance and [reserve a static IP](https://cloud.google.com/compute/docs/ip-addresses/reserve-static-internal-ip-address)
1. Check out the latest code from the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies/) with `git clone https://github.com/GoogleCloudPlatform/fda-mystudies.git` and your [personal access token](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token) as password (you may need to install git, for example `sudo apt install git`)
1. Create a Cloud SQL instance with MySQL v5.7 ([instructions](https://cloud.google.com/sql/docs/mysql/create-instance))
1. Configure the `Study datastore` database on the Cloud SQL instance
    -    Create a user account that the `Study datastore` and `Study builder` applications will use to access this instance ([instructions](https://cloud.google.com/sql/docs/mysql/create-manage-users))
    -   Run `sudo ./create_superadmin.sh <email> <password>` in the `/study-builder/sqlscript/` directory to create `sb-superadmin.sql`, which you will use to create the first `Study builder` user
(you may need to install [htpasswd](https://httpd.apache.org/docs/2.4/programs/htpasswd.html), for example `sudo apt-get install apache2-utils`)
    -    Run the [`/study-builder/HPHC_My_Studies_DB_Create_Script.sql`](/study-builder/sqlscript/HPHC_My_Studies_DB_Create_Script.sql) script to create a database named `fda_hphc` ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file)) 
    -    Run the [`/study-builder/version_info_script.sql`](/study-builder/sqlscript/version_info_script.sql) script
    -    Run the [`/study-builder/procedures.sql`](/study-builder/sqlscript/procedures.sql) script
    -    Run the `sb-superadmin.sql` script that you created in the step above
    -   Enable the database’s private IP connectivity in the same network as your VM ([instructions](https://cloud.google.com/sql/docs/mysql/configure-private-ip))
1. Configure blob storage for public study resources by [creating](https://cloud.google.com/storage/docs/creating-buckets) a Google Cloud Storage bucket with [public read access](https://cloud.google.com/storage/docs/access-control/making-data-public#buckets)
1. [Initialize a container](https://github.com/GoogleCloudPlatform/cloud-sdk-docker) with Google Cloud credentials by running `sudo docker run -ti --name gcloud-config google/cloud-sdk gcloud init`, then select a service account with access to the Google Cloud Storage bucket that you created (the VM’s [default service](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) service account will have the necessary permissions if your bucket was created in the same Google Cloud project as your VM instance)
1. Deploy the `Study datastore` container to the VM
    -    Create the Docker image using `sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -Dimage=study-datastore-image` from the `study-datastore/` directory (you may need to [install Docker](https://docs.docker.com/engine/install/debian/) and Maven, for example `sudo apt install maven`)
    -    Update the Docker environment file [`variables.env`](variables.env) with values to configure the [`application.properties`](src/main/resources/application.properties) file for your deployment
    -    Run the container on the VM using `sudo docker run --detach --volumes-from gcloud-config --env-file variables.env -p 80:8080 --name study-datastore study-datastore-image`
1. Test if the application is running by running `curl 0.0.0.0/study-datastore/healthCheck`

***
<p align="center">Copyright 2020 Google LLC</p>
