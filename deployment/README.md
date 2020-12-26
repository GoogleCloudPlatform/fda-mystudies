<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

### Introduction

This guide provides instructions for semi-automated deployment of FDA MyStudies to Google Cloud. It is designed to be completed in just a few hours.

This document explains how to integrate [Terraform’s](https://www.terraform.io/) open-source infrastructure-as-code with continuous integration and continuous deployment (CICD) for a reproducible and easy-to-maintain environment. [Kubernetes](https://kubernetes.io/) has been selected as the open-source container orchestration tool for its robust scaling and cluster management. All necessary Terraform templates, setup scripts and Kubernetes configuration files are included in the repository. This guide will explain what cloud services to enable, which configuration parameters to update and provide step-by-step instructions for each manual portion of the process.

This approach to deployment is based on Google Cloud's
[HIPAA-aligned architecture](https://cloud.google.com/solutions/architecture-hipaa-aligned-project), which is designed to simplify your compliance journey. You can learn more about this approach to compliance in the [*Setting up a HIPAA-aligned project*](https://cloud.google.com/solutions/setting-up-a-hipaa-aligned-project)
solution guide.

### Deployment overview

Following this guide will result in your own unique instance of the FDA MyStudies platform. The resulting deployment will have the structure illustrated in [**Figure 1**](#figure-1-overall-architecture-of-the-semi-automated-deployment). Each functionally distinct aspect of the platform is deployed into its own cloud project to facilitate compartmentalization and robust access management. Each project and resource is named for its purpose, and has a `{PREFIX}-{ENV}` label, where `{PREFIX}` is a consistent name of your choice and `{ENV}` delineates your environment (for example, `dev` or `prod`). The list of projects you will create for your deployment is as follows:

Project | Name | Purpose
---------|------------|---------------
Devops | `{PREFIX}-{ENV}-devops` | tbd
Apps | `{PREFIX}-{ENV}-apps` | tbd
Data | `{PREFIX}-{ENV}-data` | tbd
Firebase | `{PREFIX}-{ENV}-firebase` | tbd
Networks | `{PREFIX}-{ENV}-networks` | tbd
Secrets | `{PREFIX}-{ENV}-secrets` | tbd
Audit | `{PREFIX}-{ENV}-audit` | tbd

The `{PREFIX}-{ENV}-apps` project is where the various FDA MyStudies applications will run. [**Figure 2**](#figure-2-application-architecture) diagrams each the applications and how they related to their data sources. This deployment configures the applications URLs as follows:
Application | URL | Notes
--------------|-----------|-----------
[Study builder](/study-builder/) | `studies.{PREFIX}.{DOMAIN}/study-builder` | This URL navigates an administrative user to the `Study builder` user interface
[Study datastore](/study-datastore/) | `studies.{PREFIX}.{DOMAIN}/study-datastore` | This URL is for the `Study datastore` back-end service
[Participant manager](/participant-manager/) | `participants.{PREFIX}.{DOMAIN}/participant-manager` | This URL navigates an administrative user to the `Participant manager` user interface
[Participant manager datastore](/participant-manager-datastore/) | `participants.{PREFIX}.{DOMAIN}/participant-manager-datastore` | This URL is for the `Participant manager datastore` back-end service
[Participant datastore](/participant-datastore/) | `participants.{PREFIX}.{DOMAIN}/participant-user-datastore`<br/>`participants.{PREFIX}.{DOMAIN}/participant-enroll-datastore`<br/>`participants.{PREFIX}.{DOMAIN}/participant-consent-datastore` | These URLs are for the `Participant datastore` back-end services
[Response datastore](/response-datastore/) | `participants.{PREFIX}.{DOMAIN}/response-datastore` | This URL is for the `Response datastore` back-end service
[Auth server](/auth-server/) | `participants.{PREFIX}.{DOMAIN}/auth-server` | This URL is for the administrative users and study participants to log into their respective applications
[Hydra](/hydra/) | `participants.{PREFIX}.{DOMAIN}/oauth2` | This URL is used by the `Auth server` to complete OAuth 2.0 consent flows

More information about the purpose of each application can be found in the [*Platform Overview*](/documentation/architecture.md) guide. Detailed information about configuration and operation of each application can be found in their [respective READMEs](/documentation/README.md).

#### Figure 1: Overall architecture of the semi-automated deployment
![Architecture](/documentation/images/deployment-reference-architecture.svg "Architecture")

#### Figure 2: Application architecture
![Applications](/documentation/images/apps-reference-architecture.svg "Applications")

The deployment process takes the following approach:
1. Create a copy of the FDA MyStudies repository that you will use for your deployment
1. Create the `devops` cloud project that will be used to orchestrate your deployment
1. Connect your cloned FDA MyStudies repository to your `devops` project and set up the CICD pipelines that will automate the rest of your deployment
1. Provision the necessary cloud resources using your CICD pipelines
1. Set up a second CICD pipeline that will automate creation of your application containers
1. Create a Kubernetes cluster to run your application containers
1. Create your initial user accounts and configure the required certificates, secrets, URLs, policies and network mesh
1. Customize branding and text content as desired
1. Configure and deploy your mobile applications

### Before you begin

1. Familiarize yourself with:
    -    [Terraform](https://www.terraform.io/) and [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine)
    -    [Kubernetes](https://kubernetes.io/) and [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs/how-to/cluster-access-for-kubectl)
    -    [CICD](https://en.wikipedia.org/wiki/CI/CD) and [Google Cloud Build](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build)
    -    [IAM](https://en.wikipedia.org/wiki/Identity_management) and Google Cloud’s [resource hierarchy](https://cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy)
1. Understand how the Terraform config files and cloud resources are named and organized in the deployment:
    -  {PREFIX} is a name you choose for your deployment that will be prepended to various directories, cloud resources and URLs (for example this could be `mystudies`)
    -  {ENV} is a label you choose that will be appended to `{PREFIX}` in your directories and cloud resources (for example this could be `dev`, `test` or `prod`)
    - {DOMAIN} is the domain you will be using for your URLs (for example, “your_company_name.com” or “your_medical_center.edu”)
    - [`deployment.hcl`](/deployment/deployment.hcl) is the file where you will specify the top-level parameters for your deployment (for example, `{PREFIX}`, `{ENV}` and `{DOMAIN}`)
    - [`mystudies.hcl`](/deployment/mystudies.hcl) is the file that represents the overall recipe for the deployment (you will uncomment various aspects of this recipe as your deployment progresses)
    - The directories created in [`/deployment/terraform/`](/deployment/terraform/) by the `tfengine` command represent distinct cloud projects that the CICD pipeline monitors to create, update or destroy resources based on the changes you make to those directories
    -  The other directories in the FDA MyStudies repository map to the various components of the platform and contain Terraform and Kubernetes configuration files, such as `tf-deployment.yaml` and `tf-service.yaml`, that support each component’s deployment

### Prepare Google Cloud Platform

1. Make sure you have access to a Google Cloud environment that contains an [organization resource](https://cloud.google.com/resource-manager/docs/creating-managing-organization#acquiring) (if you haven’t already, you may need to [create](https://support.google.com/a/answer/9983832) a Google Workspace and select a domain)
1. Confirm that the billing account that you will be using has [quota](https://support.google.com/cloud/answer/6330231?hl=en) for 10 or more projects (newly created billing accounts may default to a 3-5 project quota)
    - You can test how many projects your billing account can support by manually [creating projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects) and [linking them](https://cloud.google.com/billing/docs/how-to/modify-project#enable_billing_for_a_project) to your billing account, if you are able to link 10 projects to your billing account then you can proceed, otherwise [request additional quota](https://support.google.com/code/contact/billing_quota_increase) (don’t forget to unlink the test projects from your billing account, otherwise your quota may be exhausted) 
1. [Create a folder](https://cloud.google.com/resource-manager/docs/creating-managing-folders) that you will deploy your FDA MyStudies infrastructure into (or have your Google Cloud administrator do this for you - the [`Folder Admin`](https://cloud.google.com/resource-manager/docs/access-control-folders) role for the organization is required)
1. Confirm you have access to a user account with the following Cloud IAM roles:
    - `roles/resourcemanager.folderAdmin` for the folder you created
    - `roles/resourcemanager.projectCreator` for the folder you created
    - `roles/compute.xpnAdmin` for the folder you created
    - `roles/billing.admin` for the billing account that you will use
1. [Create](https://support.google.com/a/answer/33343?hl=en) the following
    administrative [IAM](https://cloud.google.com/iam/docs/overview#concepts_related_identity) groups that will be used during deployment:
    - `{PREFIX}-{ENV}-folder-admins@{DOMAIN}`: Members of this group have the [resourcemanager.folderAdmin](https://cloud.google.com/iam/docs/understanding-roles#resource-manager-roles) role for your deployment’s folder (for example, a deployment with prefix "mystudies", environment “prod” and domain "example.com", would require a group named "mystudies-prod-folder-admins@example.com")
    - `{PREFIX}-{ENV}-devops-owners@{DOMAIN}`: Members of this group have owners access
        for the devops project, which is required to make changes to the CICD pipeline and Terraform state
    - `{PREFIX}-{ENV}-auditors@{DOMAIN}`: Members of this group have the [iam.securityReviewer](https://cloud.google.com/iam/docs/understanding-roles#iam-roles) role for your deployment’s folder, and the bigquery.user and storage.objectViewer roles for your audit log project
    - `{PREFIX}-{ENV}-cicd-viewers@{DOMAIN}`: Members of this group can view CICD results in Cloud Build, for example the results of the `terraform plan` presubmit and `terraform apply` postsubmit
    - `{PREFIX}-{ENV}-bastion-accessors@{DOMAIN}`: Members of this group have permission to access the [bastion host](https://cloud.google.com/solutions/connecting-securely#bastion) project, which provides access to the private Cloud SQL instance
   - `{PREFIX}-{ENV}-project-owners@{DOMAIN}`: Members of this group have owners access to each of the deployment’s projects



### Set up your environment

1. You can work in an existing environment, or you can configure a new environment by [creating](https://cloud.google.com/compute/docs/instances/create-start-instance) a VM instance in the Google Cloud project of your choice
1. Confirm you have the following dependencies installed and added to `$PATH`:
         - [Install](https://cloud.google.com/sdk/docs/install) the Google Cloud command line tool `gcloud`, for example `apt-get install google-cloud-sdk` (already installed if using a Google Compute Engine VM)
    - [Install](https://cloud.google.com/storage/docs/gsutil_install) the Cloud Storage command line tool `gsutil` (already installed if using a Google Compute Engine VM)
    - [Install](https://kubernetes.io/docs/tasks/tools/install-kubectl) the Kubernetes command line tool `kubectl`, for example:
```bash
sudo apt-get update && sudo apt-get install -y apt-transport-https gnupg2 curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubectl
```
         - Install [`Terraform 0.12.29`](https://learn.hashicorp.com/tutorials/terraform/install-cli), for example:
```shell
sudo apt-get install software-properties-common
curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
sudo apt-get update && sudo apt-get install terraform=0.12.29
```
         - Install [`Go 1.14+`](https://golang.org/doc/install), for example:
```shell
sudo apt install wget
wget https://golang.org/dl/go1.15.6.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.15.6.linux-amd64.tar.gz
export PATH=$PATH:/usr/local/go/bin
```
    - Install [`Terraform Engine`](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine#installation), for example:
```shell
VERSION=v0.4.0
sudo wget -O /usr/local/bin/tfengine https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/releases/download/${VERSION}/tfengine_${VERSION}_linux-amd64
sudo chmod +x /usr/local/bin/tfengine
```
1. [Duplicate](https://docs.github.com/en/free-pro-team@latest/github/creating-cloning-and-archiving-repositories/duplicating-a-repository) the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies), then clone locally
1. Update [/deployment/deployment.hcl](/deployment/deployment.hcl) with the values for your deployment
1. Set the necessary environment variables, for example:
    ```bash
    export GIT_ROOT=</path/to/your/local/repo/root>
    export ENGINE_CONFIG=${GIT_ROOT}/deployment/deployment.hcl
    export MYSTUDIES_TEMPLATE=${GIT_ROOT}/deployment/mystudies.hcl
    export PREFIX=<your_deployment_prefix>
    export ENV=<your_deployment_environment>
    export LOCATION=<your_deployment_location>
    export DOMAIN=${PREFIX}.<your_deployment_domain>
    ```
1. Authenticate as a user with the permissions described above (this deployment assume requests are made as a user, rather than a service account)
    - Update your [application default credentials](https://cloud.google.com/docs/authentication/production), for example you could run `gcloud auth application-default login` (when using a Google Compute Engine VM you must update the application default credentials, otherwise requests will continue to be made with its default service account)
    - Remember to run `gcloud auth revoke` to log your user account out once your deployment is complete

### Set up DevOps and CICD

1. Generate the Terraform configuration files
    -     Set the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl) to `false`
    -    Execute the `tfengine` command to generate the configs (by default, CICD
    will look for Terraform configs under the `deployment/terraform/` directory in the
    GitHub repo, so set the `--output_path` to point to the `deployment/terraform/`
    directory inside the local root of your GitHub repository), for example:
    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
    ```
1. Create the `devops` project and Terraform state bucket, for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init
    terraform apply
    ```

1. Backup the state of the `devops` project to the newly created state bucket by setting the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl to `true` and regenerating the Terraform configs, for example:
    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init -force-copy
    ```
1. Open [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your new `devops` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers because Terraform will create them in the next step)
1. Create the CICD pipeline for your deployment (this will create the Cloud Builder triggers that will run whenever a pull request containing changes to files in `$GIT_ROOT/deployment/terraform/` is raised against the GitHub branch that you specified in [`deployment.hcl`](/deployment/deployment.hcl), for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/cicd
    terraform init
    terraform apply
    ```
### Deploy your FDA MyStudies platform  infrastructure

1. Commit your local git working directory (which now represents your desired infrastructure state) to a new branch in your cloned FDA MyStudies repository, for example using:
```bash
git checkout -b initial-deployment
git add $GIT_ROOT/deployment/terraform
git commit -m "Perform initial deployment"
git push origin initial-deployment
```

1. Trigger Cloud Build to run the Terraform pre-submits by using this new branch to [create](https://docs.github.com/en/free-pro-team@latest/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request) a pull request against the branch you specified in [`deployment.hcl`](/deployment/deployment.hcl) (you can view the status of your pre-submits and re-run jobs as necessary in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Once your pre-submits have completed successfully, and you have received code review approval, merge your pull request into the main branch to trigger the `terraform apply` post-submit operation (this operation may take up to 45 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)

### Configure your deployment’s databases

1. [Create](https://console.cloud.google.com/datastore/) a Cloud Firestore database operating in [*Native mode*](https://cloud.google.com/datastore/docs/firestore-or-datastore) in your `{PREFIX}-{ENV}-firebase` project
1. Use Terraform and CICD to create Firestore indexes, a Cloud SQL instance, user accounts and IAM role bindings
    - Uncomment the blocks for steps 5.1 through 5.6 in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
git checkout -b database-configuration
git add $GIT_ROOT/deployment/terraform
git commit -m "Configure databases"
git push origin database-configuration
```
    - Once your pull request pre-submits have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 20 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Add IAM permissions for your SQL import bucket
    - Uncomment the blocks for Steps 6 in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
git checkout -b sql-import-bucket-permissions
git add $GIT_ROOT/deployment/terraform
git commit -m "Set SQL import bucket permissions"
git push origin sql-import-bucket-permissions
```
    - Once your pull request pre-submits have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)

1. Initialize the MySQL databases for your MyStudies applications
    - Upload the necessary SQL dump files files to the `{PREFIX}-{ENV}-mystudies-sql-import` storage bucket that you created during Terraform deployment, for example:
```bash
gsutil cp \
  ${GIT_ROOT}/study-builder/sqlscript/* \
  ${GIT_ROOT}/response-datastore/sqlscript/mystudies_response_server_db_script.sql \
  ${GIT_ROOT}/participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql \
  ${GIT_ROOT}/participant-datastore/sqlscript/mystudies_participant_datastore_db_script.sql \
  ${GIT_ROOT}/hydra/sqlscript/create_hydra_db_script.sql \
  gs://${PREFIX}-${ENV}-mystudies-sql-import
```
    - Import the SQL dump files from cloud storage to your Cloud SQL instance, for example:
```bash
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/create_hydra_db_script.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/HPHC_My_Studies_DB_Create_Script.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/procedures.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/version_info_script.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_response_server_db_script.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_participant_datastore_db_script.sql -q
gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_app_info_update_db_script.sql -q
```
1. [Enable](https://console.cloud.google.com/marketplace/product/google/sqladmin.googleapis.com) the [Cloud SQL Admin API](https://cloud.google.com/sql/docs/mysql/admin-api) for your `{PREFIX}-{ENV}-apps` project

### Deploy your FDA MyStudies application infrastructure

1. Make a [request](https://cloud.google.com/compute/quotas#requesting_additional_quota) to increase the [Global Compute Engine API Backend Services quota]((https://console.cloud.google.com/iam-admin/quotas/details;servicem=compute.googleapis.com;metricm=compute.googleapis.com%2Fbackend_services;limitIdm=1%2F%7Bproject%7D)) for your `{PREFIX}-{ENV}-apps` project to 20 (if it is not already set at or beyond this value).
1. Enable CICD for the application so that changes you make to the application code will automatically build the application containers for your deployment
    - Enable [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your `{PREFIX}-{ENV}-apps` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers because Terraform will create them in the next step)
    - Uncomment  the Cloud Build triggers portion of the apps project in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
git checkout -b enable-apps-CICD
git add $GIT_ROOT/deployment/terraform
git commit -m "Enable CICD for applications"
git push origin enable-apps-CICD
```
    - Once your pull request pre-submits have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)

### Configure and deploy your FDA MyStudies applications

1. Update the Kubernetes and application configuration files with the values specific to your deployment
    -  Replace the `<PREFIX>`, `<ENV>` and `<LOCATION>` values for each `tf-deployment.yaml` in your repo, for example:
```bash
find $GIT_ROOT -name 'tf-deployment.yaml' -exec sed -e 's/<PREFIX>-<ENV>/'$PREFIX'-'$ENV'/g' -e 's/<LOCATION>/'$LOCATION'/g' -i.backup '{}' \;
```
    -  Replace the `<PREFIX>`, `<ENV>` and `<DOMAIN>` values in [`/deployment/kubernetes/cert.yaml`](/deployment/kubernetes/cert.yaml) and [`/deployment/kubernetes/ingress.yaml`](/deployment/kubernetes/ingress.yaml), for example:
```bash
sed -e 's/<PREFIX>/'$PREFIX'/g' -e 's/<ENV>/'$ENV'/g' -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup $GIT_ROOT/deployment/kubernetes/cert.yaml
sed -e 's/<PREFIX>/'$PREFIX'/g' -e 's/<ENV>/'$ENV'/g' -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup $GIT_ROOT/deployment/kubernetes/ingress.yaml
```
    - In [`/participant-manager/src/environments/environment.prod.ts`](/participant-manager/src/environments/environment.prod.ts), replace `<DOMAIN>` with your `participants.{DOMAIN}` value and `<auth-server-client-id>` with the value of your `auto-auth-server-client-id` secret (you can find this value in the [Secret Manager](https://console.cloud.google.com/security/secret-manager/) of your `{PREFIX}-{ENV}-secrets` project) ,  for example:
```bash
export auth_server_client_id=<YOUR_VALUE>
sed -e 's/<DOMAIN>/participants.'$DOMAIN'/g' -e 's/<auth-server-client-id>/'$auth_server_client_id'/g' -i.backup $GIT_ROOT/participant-manager/src/environments/environment.prod.ts
```
    - Commit the changes to your repo, for example:
```bash
git checkout -b configure-application-properties
git add $GIT_ROOT
git commit -m "Initial configuration of application properties"
git push origin configure-application-properties
```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to build your container images, after which they will be available in the Container Registry of your apps project at http://gcr.io/{PREFIX}-{ENV}-apps (this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `{PREFIX}-{ENV}-apps` project)
1. Open [Secret Manager](https://console.cloud.google.com/security/secret-manager) for your `{PREFIX}-{ENV}-secrets` project and fill in the values for the secrets with prefix `manual-`
    - discuss different categories of secrets here and recommended values / what they do
Manually set secret | Description | When to set
--------------------------|-------------------|------------------------------------------------------------
manual-mystudies-email-address | The login of the email account you want MyStudies to use to send system-generated emails
manual-mystudies-email-password | The password for that email account
manual-mystudies-contact-email-address | The email address that the in-app contact and feedback forms will send messages to  
manual-mystudies-from-email-address | The return email address that is shown is system-generated messages (you may want to use a no-reply@ address)
manual-mystudies-from-email-domain | The domain of the above email address (just the value after “@”)
manual-mystudies-smtp-hostname | The hostname for your email account’s SMTP server (for example, smtp.gmail.com)
manual-mystudies-smtp-use-ip-allowlist | Typically ‘False’; if ‘True’, the platform will not authenticate to the email server and will rely on the allowlist configured in the SMTP service
manual-log-path | The path to a directory within each application’s container where your logs will be written (for example `/logs`)  
manual-org-name | The name of your organization that is displayed to users, for example ‘Sincerely, the <manual-org-name> support team’
manual-terms-url | URL for a terms and conditions page that the applications will link to
manual-privacy-url | URL for a privacy policy page that the applications will link to
manual-fcm-api-url | # URL of your Firebase Cloud Messaging API ([documentation](https://firebase.google.com/docs/cloud-messaging/http-server-ref))
manual-mobile-app-appid | The value of the `App ID` that you will configure on the Settings page of the [`Study builder`](/study-builder/) user interface when you create your first study (you will also use this same value when configuring your mobile applications for deployment)
manual-android-bundle-id | The value of `applicationId` that you will configure in [`Android/app/build.gradle`](/Android/app/build.gradle) during [Android configuration](/Android/) 
manual-android-server-key | The Firebase Cloud Messaging server key that you will obtain during [Android configuration](/Android/)
manual-ios-bundle-id | The value you will obtain from Xcode (Project target > General tab > Identity section > Bundle identifier) during [iOS configuration](/iOS/)
manual-ios-certificate | The value of the Base64 converted `.p12` file that you will obtain during [iOS configuration](/iOS/)
manual-ios-certificate-password | The value of the password for the `.p12` certificate (necessary if your certificate is encrypted - otherwise leave empty)
manual-ios-deeplink-url | The URL to redirect to after iOS login (for example, app://{PREFIX}.{DOMAIN}/mystudies)
manual-android-deeplink-url | The URL to redirect to after Android login (for example, app://{PREFIX}.{DOMAIN}/mystudies)

1. Finish Kubernetes cluster configuration and deployment
    - Configure the remaining resources with Terraform, for example: 
```bash
cd $GIT_ROOT/deployment/terraform/kubernetes/
terraform init
terraform plan
terraform apply
```
    - Set your `kubectl` credentials, for example:
```bash
gcloud container clusters get-credentials "$PREFIX-$ENV-gke-cluster" --region=$LOCATION --project="$PREFIX-$ENV-apps"
```
    - Apply the pod security policies, for example:
```bash
  kubectl apply \
  -f $GIT_ROOT/deployment/kubernetes/pod_security_policy.yaml \
  -f $GIT_ROOT/deployment/kubernetes/pod_security_policy-istio.yaml
```
    - Apply all deployments, for example:
```bash
   kubectl apply \
  -f $GIT_ROOT/study-datastore/tf-deployment.yaml \
  -f $GIT_ROOT/response-datastore/tf-deployment.yaml \
  -f $GIT_ROOT/participant-datastore/consent-mgmt-module/tf-deployment.yaml \
  -f $GIT_ROOT/participant-datastore/enroll-mgmt-module/tf-deployment.yaml \
  -f $GIT_ROOT/participant-datastore/user-mgmt-module/tf-deployment.yaml \
  -f $GIT_ROOT/study-builder/tf-deployment.yaml \
  -f $GIT_ROOT/auth-server/tf-deployment.yaml \
  -f $GIT_ROOT/participant-manager-datastore/tf-deployment.yaml \
  -f $GIT_ROOT/hydra/tf-deployment.yaml \
  -f $GIT_ROOT/participant-manager/tf-deployment.yaml
```
    - Apply all services, for example:
```bash
   kubectl apply \
  -f $GIT_ROOT/study-datastore/tf-service.yaml \
  -f $GIT_ROOT/response-datastore/tf-service.yaml \
  -f $GIT_ROOT/participant-datastore/consent-mgmt-module/tf-service.yaml \
  -f $GIT_ROOT/participant-datastore/enroll-mgmt-module/tf-service.yaml \
  -f $GIT_ROOT/participant-datastore/user-mgmt-module/tf-service.yaml \
  -f $GIT_ROOT/study-builder/tf-service.yaml \
  -f $GIT_ROOT/auth-server/tf-service.yaml \
  -f $GIT_ROOT/participant-manager-datastore/tf-service.yaml \
  -f $GIT_ROOT/hydra/tf-service.yaml \
  -f $GIT_ROOT/participant-manager/tf-service.yaml
```
    - Apply the certificate and the ingress, for example:
```bash
   kubectl apply \
  -f $GIT_ROOT/deployment/kubernetes/cert.yaml \
  -f $GIT_ROOT/deployment/kubernetes/ingress.yaml
```
    - Update Firewalls - as of now there is a known issue with Firewalls in ingress-gce (references [kubernetes/ingress-gce#485](https://github.com/kubernetes/ingress-gce/issues/485) and [kubernetes/ingress-gce#584](https://github.com/kubernetes/ingress-gce/issues/584)
    - Run `kubectl describe ingress $PREFIX-$ENV`
    - Look at the suggested commands under "Events", in the form of "Firewall
        change required by network admin: `<gcloud command>`".
    - Run each of the suggested commands.
1. Check the [Kubernetes dashboard](https://console.cloud.google.com/kubernetes/workload) in your `{PREFIX}-{ENV}-apps` project to view the status of your deployment
    - 
1. Configure your initial application credentials
    - Create the [`Hydra`](/hydra/) credentials for server-to-server requests by running [register_clients_in_hydra.sh](/deployment/scripts/register_clients_in_hydra.sh), for example: ```bash
$GIT_ROOT/deployment/scripts/register_clients_in_hydra.sh $PREFIX $ENV https://participants.$DOMAIN
```
    - Create your first admin user account for the [`Participant manager`](/participant-manager/) application by running the [`create_participant_manager_superadmin.sh`](/deployment/scripts/create_participant_manager_superadmin.sh) script to generate and import a SQL dump file for the [`Participant datastore`](/participant-datastore/) database, for example:
```bash
$GIT_ROOT/deployment/scripts/create_participant_manager_superadmin.sh $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
```
    - Create your first admin user account for the [`Study builder`](/study-builder/) application by running the [`create_study_builder_superadmin.sh`](/deployment/scripts/create_study_builder_superadmin.sh) script to generate and import a SQL dump file for the [`Study datastore`](/study-datastore/) database, for example:
```bash
$GIT_ROOT/deployment/scripts/create_study_builder_superadmin.sh $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
```

### Set up mobile applications

1. Build and distribute iOS and Android apps following their individual
    instructions. See [iOS](../iOS/README.md) and [Android](../Android/README.md) 
    configuration instructions.

Add XYZ values to the secret manager
   
   
### Step 12: Mobile app setup in participant manager

An app record is a representation of your mobile apps associated with an 
FDA MyStudies deployment. App is identified by APP_ID, which is the value you 
set in secret manager for `manual-mobile-app-appid`.

After a study is created (in study builder) that uses this App ID, a corresponding 
app record will be created in the Participant Manager.

**Note** Current deployment only supports a single App 
(using `manual-mobile-app-appid`); and it requires the following
manual step to pass mobile info from Secret Manager to CloudSQL.

1. Once the app is available in Participant Manager, Run
    [copy_app_info_to_sql.sh](scripts/copy_app_info_to_sql.sh)
    passing your deployment PREFIX and ENV.
    
    The secrets accessed by this script are: 
    ```bash
    # bundleID used for the Android App.
    manual-android-bundle-id
    # found under settings > cloud messaging in the android app defined in your firebase project.
    manual-android-server-key
    # bundleID used to build and distribute the iOS App.
    manual-ios-bundle-id
    # push notifications certificate in encrypted .p12 format.
    manual-ios-certificate
    # push notifications certificate password.
    manual-ios-certificate-password
    # redirect links to mobile apps, e.g. app://mydeploymentdomain.com/mystudies
    manual-ios-deeplink-url
    manual-android-deeplink-url
    ```
### Confirm deployment
 

### Clean up

1. Remove your user account from the groups you no longer need access to
1. Revoke user access in your environment by running `gcloud auth revoke`

***
<p align="center">Copyright 2020 Google LLC</p>
