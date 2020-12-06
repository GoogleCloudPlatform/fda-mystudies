<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
The **FDA MyStudies** [`Participant manager datastore`](/participant-manager-datastore/) provides the backend APIs that the [`Participant manager`](/participant-manager/) web application uses to create and maintain participants, studies and sites. The `Participant manager datastore` is a Java Spring boot application that shares a MySQL backend database with the [`Participant datastore`](/participant-datastore/). The `Participant manager datastore` uses basic authentication `client_id` and `client_secret` that are provided to client applications and managed by [`Hydra`](/hydra/).
 
The `Participant manager datastore` client application is the [`Participant manager`](/participant-manager/) user interface. Interaction with other platform components is through the shared [`Participant datastore`](/participant-datastore/) database.
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
To deploy the [`Participant manager datastore`](/participant-manager-datastore/) manually:
1. [Create](https://cloud.google.com/compute/docs/instances/create-start-instance) a Compute Engine VM instance and [reserve a static IP](https://cloud.google.com/compute/docs/ip-addresses/reserve-static-internal-ip-address)
1. Check out the latest code from the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies/) with `git clone https://github.com/GoogleCloudPlatform/fda-mystudies.git` and your [personal access token](https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token) as password (you may need to install git, for example `sudo apt install git`)
1. [Initialize a container](https://github.com/GoogleCloudPlatform/cloud-sdk-docker) with Google Cloud credentials by running `sudo docker run -ti --name gcloud-config google/cloud-sdk gcloud init`, then select a service account with access to the Google Cloud Storage bucket that you created for consent forms during your `Participant datastore` deployment (the VM’s [default service](https://cloud.google.com/compute/docs/access/service-accounts#default_service_account) service account will have the necessary permissions if your bucket was created in the same Google Cloud project as your VM instance)
1. Deploy the `Participant manager datastore` container to the VM
    -    Create the Docker image using `sudo mvn -B package -Pprod com.google.cloud.tools:jib-maven-plugin:2.5.2:dockerBuild -Dimage=participant-manager-datastore-image` from the `participant-manager-datastore/` directory (you may need to [install Docker](https://docs.docker.com/engine/install/debian/) and Maven, for example `sudo apt install maven`)
    -    Update the Docker environment file [`variables.env`](variables.env) with values to configure the [`application.properties`](participant-manager-service/src/main/resources/application.properties) file for your deployment
    -    Run the container on the VM using `sudo docker run --detach --volumes-from gcloud-config --env-file variables.env -p 80:8080 --name participant-manager-datastore participant-manager-datastore-image`
1. Test if the application is running with `curl http://0.0.0.0/participant-manager-datastore/healthCheck`

***
<p align="center">Copyright 2020 Google LLC</p>
