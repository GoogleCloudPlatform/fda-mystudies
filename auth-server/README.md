<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
The **FDA MyStudies** [`Auth server`](../auth-server/) is the centralized authentication mechanism for the various client applications of the MyStudies platform.
 
The client applications are:
1. [`Android mobile application`](../Android/)
1. [`iOS mobile application`](../iOS/)
1. [`Participant manager`](../participant-manager/)
 
The `Auth server`provides the following functionality:
1. User registration
1. User credentials management
1. User authentication
1. User logout
1. Server-to-server authentication support
 
The `Auth server` identity management application is built as a Spring Boot application that implements user login and consent flows. It integrates with your deployment’s instance of [ORY Hydra](https://www.ory.sh/hydra/) for token generation and management.
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
To deploy the [`Auth server`](/auth-server/) manually:
1. [Create](https://cloud.google.com/compute/docs/instances/create-start-instance) a Compute Engine VM instance and [reserve a static IP](https://cloud.google.com/compute/docs/ip-addresses/reserve-static-internal-ip-address)
1. Check out the latest code from the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies/)
1. Create a Cloud SQL instance with MySQL v5.7 ([instructions](https://cloud.google.com/sql/docs/mysql/create-instance))
1. Configure the `Auth server` database on the Cloud SQL instance
    -    Create a user account that the `Auth server` application will use to access this instance ([instructions](https://cloud.google.com/sql/docs/mysql/create-manage-users))
    -    Create a database named `oauth_server_hydra` with the [`mystudies_oauth_server_hydra_db_script.sql`](sqlscript/mystudies_oauth_server_hydra_db_script.sql) script ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file))
    -   Enable the database’s private IP connectivity in the same network as your VM ([instructions](https://cloud.google.com/sql/docs/mysql/configure-private-ip))
1. Deploy the `Auth server` container to the VM
    -    Create the Docker image using `sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -Dimage=auth-server-image` from the `auth-server/` directory (you may need to [install Docker](https://docs.docker.com/engine/install/debian/) and Maven, for example `sudo apt install maven`)
    -    Update the Docker environment file [`variables.env`](variables.env) with the values of the [`application.properties`](oauth-scim-service/src/main/resources/application.properties) file for your deployment
    -    Run the container on the VM using `sudo docker run --detach --env-file variables.env -p 80:8080 --name auth-server auth-server-image`
1. Test if the application is running with `curl http://0.0.0.0/auth-server/healthCheck`

***
<p align="center">Copyright 2020 Google LLC</p>
