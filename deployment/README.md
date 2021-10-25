<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

## Deploying FDA MyStudies
### Introduction

This guide provides instructions for semi-automated deployment of FDA MyStudies to Google Cloud. It is designed to be completed in just a few hours.

This document explains how to integrate [Terraform’s](https://www.terraform.io/) open-source infrastructure-as-code with continuous integration and continuous deployment (CICD) for a reproducible and easy-to-maintain environment. [Kubernetes](https://kubernetes.io/) has been selected as the open-source container orchestration tool for its robust scaling and cluster management. All necessary Terraform templates, setup scripts and Kubernetes configuration files are included in the repository. This guide will explain what cloud services to enable, which configuration parameters to update and provide step-by-step instructions for each manual portion of the process.

This approach to deployment is based on Google Cloud's
[HIPAA-aligned architecture](https://cloud.google.com/solutions/architecture-hipaa-aligned-project), which is designed to simplify your compliance journey. You can learn more about this approach to compliance in the [*Setting up a HIPAA-aligned project*](https://cloud.google.com/solutions/setting-up-a-hipaa-aligned-project)
solution guide.

### Deployment overview

Following this guide will result in your own unique instance of the FDA MyStudies platform. The resulting deployment will have the structure illustrated in [**Figure 1**](#figure-1-overall-architecture-of-the-semi-automated-deployment). Each functionally distinct aspect of the platform is deployed into its own cloud project to facilitate compartmentalization and robust access management. Each project and resource is named for its purpose, and has a `{PREFIX}-{ENV}` label, where `{PREFIX}` is a consistent name of your choice and `{ENV}` describes your environment (for example, `dev` or `prod`). The list of projects you will create for your deployment is as follows:

Project | Name | Purpose
---------|------------|---------------
Devops | `{PREFIX}-{ENV}-devops` | This project executes the Terraform CICD pipeline that keeps your infrastructure aligned with the state defined in the [`deployment/terraform/`](/deployment/terraform/) directory of your GitHub repository
Apps | `{PREFIX}-{ENV}-apps` | This project stores the container images for each of your FDA MyStudies applications, updates those images with CICD pipelines that monitor changes you make to the application directories of your GitHub repository, and administers the Kubernetes cluster that operates those images ([**Figure 2**](#figure-2-application-architecture) diagrams each the applications and how they related to their data sources)
Data | `{PREFIX}-{ENV}-data` | This project contains the MySQL databases that support each of the FDA MyStudies applications, and the blob storage buckets that hold study resources and consent documents
Firebase | `{PREFIX}-{ENV}-firebase` | This project contains the NoSQL database that stores the study response data
Networks | `{PREFIX}-{ENV}-networks` | This project manages the network policies and firewalls 
Secrets | `{PREFIX}-{ENV}-secrets` | This project manages the deployment’s secrets, such as client ids and client secrets
Audit | `{PREFIX}-{ENV}-audit` | This project stores the audit logs for the FDA MyStudies platform and applications

This deployment configures the applications URLs as follows:

Application | URL | Notes
--------------|-----------|-----------
[Study builder](/study-builder/) | `studies.{PREFIX}-{ENV}.{DOMAIN}/study-builder` | This URL navigates an administrative user to the `Study builder` user interface
[Study datastore](/study-datastore/) | `studies.{PREFIX}-{ENV}.{DOMAIN}/study-datastore` | This URL is for the `Study datastore` back-end service
[Participant manager](/participant-manager/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/participant-manager` | This URL navigates an administrative user to the `Participant manager` user interface
[Participant manager datastore](/participant-manager-datastore/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/participant-manager-datastore` | This URL is for the `Participant manager datastore` back-end service
[Participant datastore](/participant-datastore/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/participant-user-datastore`<br/>`participants.{PREFIX}-{ENV}.{DOMAIN}/participant-enroll-datastore`<br/>`participants.{PREFIX}-{ENV}.{DOMAIN}/participant-consent-datastore` | These URLs are for the `Participant datastore` back-end services
[Response datastore](/response-datastore/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/response-datastore` | This URL is for the `Response datastore` back-end service
[Auth server](/auth-server/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/auth-server` | This URL is for the administrative users and study participants to log into their respective applications
[Hydra](/hydra/) | `participants.{PREFIX}-{ENV}.{DOMAIN}/oauth2` | This URL is used by the `Auth server` to complete OAuth 2.0 consent flows

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
1. Create your first study
1. Configure and deploy your mobile applications

### Before you begin

1. Familiarize yourself with:
    -    [Terraform](https://www.terraform.io/) and [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine)
    -    [Kubernetes](https://kubernetes.io/) and [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs/how-to/cluster-access-for-kubectl)
    -    [CICD](https://en.wikipedia.org/wiki/CI/CD) and [Google Cloud Build](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build)
    -    [IAM](https://en.wikipedia.org/wiki/Identity_management) and Google Cloud’s [resource hierarchy](https://cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy)
1. Understand how the Terraform config files and cloud resources are named and organized in the deployment:
    -  `{PREFIX}` is a name you choose for your deployment that will be prepended to various directories, cloud resources and URLs (for example this could be ‘mystudies’)
    -  `{ENV}` is a label you choose that will be appended to `{PREFIX}` in your directories and cloud resources (for example this could be ‘dev’, ‘test’ or ‘prod’)
    - `{DOMAIN}` is the domain you will be using for your URLs (for example, ‘your_company_name.com’ or ‘your_medical_center.edu’)
    - [`/deployment/deployment.hcl`](/deployment/deployment.hcl) is the file where you will specify top-level parameters for your deployment (for example, the values of `{PREFIX}`, `{ENV}` and `{DOMAIN}`)
    - [`/deployment/mystudies.hcl`](/deployment/mystudies.hcl) is the file that represents the overall recipe for the deployment (you will uncomment various aspects of this recipe as your deployment progresses)
    - The directories created in [`/deployment/terraform/`](/deployment/terraform/) by the `tfengine` command represent distinct cloud projects that the CICD pipeline monitors to create, update or destroy resources based on the changes you make to those directories
    -  The other directories in the FDA MyStudies repository map to the various components of the platform and contain Terraform and Kubernetes configuration files, such as `tf-deployment.yaml` and `tf-service.yaml`, that support each component’s deployment

### Prepare the cloud platform

1. Make sure you have access to a Google Cloud environment that contains an [organization resource](https://cloud.google.com/resource-manager/docs/creating-managing-organization#acquiring) (if you don’t have an organization resource, you can obtain one by [creating](https://support.google.com/a/answer/9983832) a Google Workspace and selecting a domain)
1. Confirm the billing account that you will use has [quota](https://support.google.com/cloud/answer/6330231?hl=en) for 10 or more projects (newly created billing accounts may default to a 3-5 project quota)
    - You can test how many projects your billing account can support by manually [creating projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects) and [linking them](https://cloud.google.com/billing/docs/how-to/modify-project#enable_billing_for_a_project) to your billing account, if you are able to link 10 projects to your billing account then you can proceed, otherwise [request additional quota](https://support.google.com/code/contact/billing_quota_increase) (don’t forget to unlink the test projects from your billing account, otherwise your quota may be exhausted) 
1. Use the [resource manager](https://console.cloud.google.com/cloud-resource-manager) to [create a folder](https://cloud.google.com/resource-manager/docs/creating-managing-folders) to deploy your FDA MyStudies infrastructure into, for example you could name this folder `{PREFIX}-{ENV}` (if you do not have the [`resourcemanager.folderAdmin`](https://cloud.google.com/resource-manager/docs/access-control-folders) role for your organization, you may need to ask your Google Cloud IT administrator to do this for you)
1. Confirm you have access to a user account with the following Cloud IAM roles:
    - `roles/resourcemanager.folderAdmin` for the folder you created
    - `roles/resourcemanager.projectCreator` for the folder you created
    - `roles/compute.xpnAdmin` for the organization (note, this permission must be at the organization level not the folder level)
    - `roles/billing.admin` for the billing account that you will use
1. Use the [groups manager](https://console.cloud.google.com/identity/groups) to [create](https://support.google.com/a/answer/33343?hl=en) the following
    administrative [IAM](https://cloud.google.com/iam/docs/overview#concepts_related_identity) groups that will be used during deployment:

    Group name | Description
    ------------------|----------------
    `{PREFIX}-{ENV}-folder-admins@{DOMAIN}` | Members of this group have the [resourcemanager.folderAdmin](https://cloud.google.com/iam/docs/understanding-roles#resource-manager-roles) role for your deployment’s folder (for example, a deployment with prefix `mystudies`, environment `prod` and domain `example.com`, would require a group named `mystudies-prod-folder-admins@example.com`)
    `{PREFIX}-{ENV}-devops-owners@{DOMAIN}` | Members of this group have owners access for the devops project, which is required to make changes to the CICD pipeline and Terraform state
    `{PREFIX}-{ENV}-auditors@{DOMAIN}` | Members of this group have the [`iam.securityReviewer`](https://cloud.google.com/iam/docs/understanding-roles#iam-roles) role for your deployment’s folder, and the `bigquery.user` and `storage.objectViewer` roles for your audit log project
    `{PREFIX}-{ENV}-cicd-viewers@{DOMAIN}` | Members of this group can view CICD results in Cloud Build, for example the results of the `terraform plan` presubmit and `terraform apply` postsubmit
    `{PREFIX}-{ENV}-bastion-accessors@{DOMAIN}` | Members of this group have permission to access the [bastion host](https://cloud.google.com/solutions/connecting-securely#bastion) project, which provides access to the private Cloud SQL instance
    `{PREFIX}-{ENV}-project-owners@{DOMAIN}` | Members of this group have owners access to each of the deployment’s projects
1. Add the user account that you will be using for deployment to these groups (if it is not already a member)

### Set up your environment

1. You can work in an existing environment, or you can configure a new environment by [creating](https://cloud.google.com/compute/docs/instances/create-start-instance) a VM instance in the Google Cloud project of your choice (for example, an `e2-medium` GCE VM with Debian GNU/Linux 10 and default settings)
1. Confirm you have the following dependencies installed and added to your `$PATH`:
    - [Install](https://cloud.google.com/sdk/docs/install) the Google Cloud command line tool `gcloud` (already installed if using a Google Compute Engine VM), for example:
         ```bash
         apt-get install google-cloud-sdk
         ```
    - [Install](https://cloud.google.com/storage/docs/gsutil_install) the Cloud Storage command line tool `gsutil` (already installed if using a Google Compute Engine VM)
    - [Install](https://kubernetes.io/docs/tasks/tools/install-kubectl) the Kubernetes command line tool `kubectl`, for example:
         ```bash
         sudo apt-get update && sudo apt-get install -y apt-transport-https gnupg2 curl && \
           curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add - && \
           echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list && \
           sudo apt-get update && \
           sudo apt-get install -y kubectl
         ```
    - Install [Terraform 0.12.29](https://learn.hashicorp.com/tutorials/terraform/install-cli), for example:
         ```shell
         sudo apt-get install software-properties-common -y && \
           curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add - && \
           sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" && \
           sudo apt-get update && sudo apt-get install terraform=0.12.29
         ```
    - Install [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine#installation), for example:
         ```shell
         VERSION=v0.4.0 && \
           sudo apt install wget -y && \
           sudo wget -O /usr/local/bin/tfengine https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/releases/download/${VERSION}/tfengine_${VERSION}_linux-amd64 && \
           sudo chmod +x /usr/local/bin/tfengine
         ```
    - Install [Git](https://github.com/git-guides/install-git), for example:
         ```shell
         sudo apt-get install git
         ```
1. [Duplicate](https://docs.github.com/en/free-pro-team@latest/github/creating-cloning-and-archiving-repositories/duplicating-a-repository) the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies), then clone it locally
1. Update [`/deployment/deployment.hcl`](/deployment/deployment.hcl) with the values for your deployment
1. Update [`/deployment/scripts/set_env_var.sh`](/deployment/scripts/set_env_var.sh) for your deployment, then use the script to set your environment variables, for example:
    ```
    source set_env_var.sh    # executed from your /deployment/scripts directory
    ```
1. Authenticate as a user with the permissions described above (this deployment assumes gcloud and Terraform commands are made as a user, rather than a service account)
    - Login and update your [application default credentials](https://cloud.google.com/docs/authentication/production), for example you could run `gcloud auth login --update-adc` (when using a Google Compute Engine VM you must update the application default credentials, otherwise requests will continue to be made with its default service account)
    - Remember to log your user account out once your deployment is complete

### Create your devops project and configure CICD pipelines

1. Generate your Terraform configuration files
    - Set the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl) to `false`, for example:
         ```bash
         sed -e 's/enable_gcs_backend = true/enable_gcs_backend = false/g' \
          -i.backup $GIT_ROOT/deployment/mystudies.hcl
         ``` 
    - Execute the `tfengine` command to generate the configs (by default, CICD
    will look for Terraform configs under the `deployment/terraform/` directory in the
    GitHub repo, so set the `--output_path` to point to the `deployment/terraform/`
    directory inside the local root of your GitHub repository), for example:
         ```bash
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         ```
1. Use Terraform to create the `{PREFIX}-{ENV}-devops` project and Terraform state bucket (if this step fails, confirm you have updated your application default credentials and that the required version of Terraform is installed), for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init && terraform apply
    ```
1. Backup the state of your `{PREFIX}-{ENV}-devops` project to the newly created state bucket by setting the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl) to `true` and regenerating the Terraform configs, for example:
    ```bash
    sed -e 's/enable_gcs_backend = false/enable_gcs_backend = true/g' \
      -i.backup $GIT_ROOT/deployment/mystudies.hcl    
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init -force-copy
    ```
1. Open [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your new `{PREFIX}-{ENV}-devops` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers as Terraform will create them in the next step)
1. Create the CICD pipeline for your deployment (this will create the Cloud Builder triggers that will run whenever a pull request containing changes to files in `$GIT_ROOT/deployment/terraform/` is raised against the GitHub branch that you specified in [`deployment.hcl`](/deployment/deployment.hcl)), for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/cicd
    terraform init && terraform apply
    ```
1. The Cloud Build service account will need to be a Shared VPC Admin (XPN Admin) at the organization level as this permission cannot be granted at the folder level.
    1. Open [Cloud IAM](https://console.cloud.google.com/iam-admin/iam) in your `{PREFIX}-{ENV}-devops` project, and copy the Member name of the service account with the role `Cloud Build Service Agent`. The format should be `############@cloudbuild.gserviceaccount.com`
    1. At the top of the page change from the `{PREFIX}-{ENV}-devops` project to the organization containing your folder and projects.
    1. Click Add to add a new member to the organization
    1. Enter the Cloud Build service account in the New members field and add the role `Compute Shared VPC Admin` and click `Save`.

### Deploy your platform infrastructure

1. Commit your local git working directory (which now represents your desired infrastructure state) to a new branch in your cloned FDA MyStudies repository, for example using:
    ```bash
    cd $GIT_ROOT
    git checkout -b initial-deployment
    git add $GIT_ROOT/deployment/terraform
    git commit -m "Perform initial deployment"
    git push origin initial-deployment
    ```
1. Trigger Cloud Build to run the Terraform pre-submit checks by using this new branch to [create](https://docs.github.com/en/free-pro-team@latest/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request) a pull request against the branch you specified in [`deployment.hcl`](/deployment/deployment.hcl) (you can view the status of your pre-submit checks and re-run jobs as necessary in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Once your pre-submit checks have completed successfully, and you have received code review approval, merge your pull request into the main branch to trigger the `terraform apply` post-submit operation (this operation may take up to 45 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)

    > Note: If your pre-submit checks or post-submit `terraform apply` fail with an error related to billing accounts, you may not have the [quota](https://support.google.com/cloud/answer/6330231?hl=en) necessary to attach all of your projects to the specified billing account. You may need to [request additional quota](https://support.google.com/code/contact/billing_quota_increase).
1. [Grant](https://cloud.google.com/iam/docs/granting-changing-revoking-access) the [`roles/owner`](https://cloud.google.com/resource-manager/docs/access-control-proj#using_basic_roles) permission to the `{PREFIX}-{ENV}-project-owners@{DOMAIN}` group for each of your newly created projects

### Configure your domain for the deployment

1. [Determine the name servers](https://cloud.google.com/dns/docs/update-name-servers#look-up-cloud-dns-name-servers) that Cloud DNS has allocated to your `{PREFIX}-{ENV}` subdomain
    - Navigate to [DNS zones](https://console.cloud.google.com/net-services/dns/zones) in your `{PREFIX}-{ENV}-apps` project
    - Click on the zone named `{PREFIX}-{ENV}`, then click `Registrar Setup` in the upper right to view the name servers allocated to your subdomain
1. Update your domain’s DNS settings with your domain registrar to create a [delegated subzone](https://cloud.google.com/dns/docs/dns-overview#delegated_subzone) with the name servers allocated by Cloud DNS (this process differs across domain registrars - consult your domain registrar’s documentation to determine how to make these changes, or ask your domain’s IT administrator for help)
    - If your domain registrar is Google Domains, you can create the delegated subzone as follows:
         1. Log into [Google Domains](https://domains.google.com/) using the account that administers your domain
         1. Navigate to the DNS page for your domain and scroll down to the ‘custom resource records’ section (you do not need to make changes in the other sections)
         1. Create an NS [resource record](https://support.google.com/domains/answer/3290350) for your {PREFIX}-{ENV} subdomain that matches the name servers specified in Cloud DNS, for example:
![Domain configuration](/documentation/images/delegated-subzone.png "Domain configuration") 
1. [Verify](https://cloud.google.com/dns/docs/tutorials/create-domain-tutorial#step-6:-verify-your-setup) your setup (it may take up to 48 hours for DNS changes to propagate across the internet)

### Configure your deployment’s databases

1. [Create](https://console.cloud.google.com/datastore/) a [*Native mode*](https://cloud.google.com/datastore/docs/firestore-or-datastore) Cloud Firestore database in your `{PREFIX}-{ENV}-firebase` project (the location selected here does not need to match the region configured in your `deployment.hcl` file) 
1. Use Terraform and CICD to create Firestore indexes, a Cloud SQL instance, user accounts and IAM role bindings
    - Uncomment the blocks for steps 5.1 through 5.6 in [`mystudies.hcl`](/deployment/mystudies.hcl), for example:
         ```bash
         sed -e 's/#5# //g' -i.backup $GIT_ROOT/deployment/mystudies.hcl
         ```
    - Regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
         cd $GIT_ROOT
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         git checkout -b database-configuration
         git add $GIT_ROOT/deployment/terraform
         git commit -m "Configure databases"
         git push origin database-configuration
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 20 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Configure the permissions of your SQL script bucket so that your Cloud SQL instance can import the necessary initialization scripts
    - Uncomment the blocks for Steps 6 in [`mystudies.hcl`](/deployment/mystudies.hcl), for example:
         ```bash
         sed -e 's/#6# //g' -i.backup $GIT_ROOT/deployment/mystudies.hcl
         ```
    - Regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
         cd $GIT_ROOT
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         git checkout -b sql-bucket-permissions
         git add $GIT_ROOT/deployment/terraform
         git commit -m "Set SQL bucket permissions"
         git push origin sql-bucket-permissions
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Initialize your MySQL databases by importing SQL scripts
    - Upload the necessary SQL script files to the `{PREFIX}-{ENV}-mystudies-sql-import` storage bucket that you created during Terraform deployment, for example:
         ```bash
         gsutil cp \
           ${GIT_ROOT}/study-builder/sqlscript/* \
           ${GIT_ROOT}/response-datastore/sqlscript/mystudies_response_server_db_script.sql \
           ${GIT_ROOT}/participant-datastore/sqlscript/mystudies_participant_datastore_db_script.sql \
           ${GIT_ROOT}/auth-server/sqlscript/mystudies_oauth_server_hydra_db_script.sql \
           ${GIT_ROOT}/hydra/sqlscript/create_hydra_db_script.sql \
           gs://${PREFIX}-${ENV}-mystudies-sql-import
         ```
    - Import the SQL scripts from cloud storage to your Cloud SQL instance, for example:
         ```bash
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/create_hydra_db_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_oauth_server_hydra_db_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/HPHC_My_Studies_DB_Create_Script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/procedures.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/version_info_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_response_server_db_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_participant_datastore_db_script.sql -q
         ```
1. [Enable](https://console.cloud.google.com/marketplace/product/google/sqladmin.googleapis.com) the [Cloud SQL Admin API](https://cloud.google.com/sql/docs/mysql/admin-api) for your `{PREFIX}-{ENV}-apps` project, for example:
    ```bash
    gcloud config set project $PREFIX-$ENV-apps && \
      gcloud services enable sqladmin.googleapis.com
    ```

### Configure and deploy your applications

1. Make a [request](https://cloud.google.com/compute/quotas#requesting_additional_quota) to increase the [Global Compute Engine API Backend Services quota]((https://console.cloud.google.com/iam-admin/quotas/details;servicem=compute.googleapis.com;metricm=compute.googleapis.com%2Fbackend_services;limitIdm=1%2F%7Bproject%7D)) for your `{PREFIX}-{ENV}-apps` project to 20 (if it is not already set at, or beyond, this value)
1. Enable CICD for the application directories of your cloned GitHub repository so that changes you make to the application code will automatically build the application containers for your deployment
    - Enable [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your `{PREFIX}-{ENV}-apps` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers as Terraform will create them in the next step)
    - Uncomment  the Cloud Build triggers portion of the apps project (step 7) in [`mystudies.hcl`](/deployment/mystudies.hcl), for example:
         ```bash
         sed -e 's/#7# //g' -i.backup $GIT_ROOT/deployment/mystudies.hcl
         ```
    - Regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
         cd $GIT_ROOT
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         git checkout -b enable-apps-CICD
         git add $GIT_ROOT/deployment/terraform
         git commit -m "Enable CICD for applications"
         git push origin enable-apps-CICD
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Update the Kubernetes and application configuration files with the values specific to your deployment
    -  Replace the `<PREFIX>`, `<ENV>` and `<LOCATION>` values for each `tf-deployment.yaml` in your repo, for example:
         ```bash
         find $GIT_ROOT -name 'tf-deployment.yaml' \
           -exec sed -e 's/<PREFIX>-<ENV>/'$PREFIX'-'$ENV'/g' \
           -e 's/<LOCATION>/'$LOCATION'/g' -i.backup '{}' \;
         ```
    -  Replace the `<PREFIX>`, `<ENV>` and `<DOMAIN>` values in [`/deployment/kubernetes/cert.yaml`](/deployment/kubernetes/cert.yaml) and [`/deployment/kubernetes/ingress.yaml`](/deployment/kubernetes/ingress.yaml), for example:
         ```bash
         sed -e 's/<PREFIX>/'$PREFIX'/g' \
           -e 's/<ENV>/'$ENV'/g' \
           -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup \
           $GIT_ROOT/deployment/kubernetes/cert.yaml
         sed -e 's/<PREFIX>/'$PREFIX'/g' \
           -e 's/<ENV>/'$ENV'/g' \
           -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup \
           $GIT_ROOT/deployment/kubernetes/ingress.yaml
         ```
    - In [`/participant-manager/src/environments/environment.prod.ts`](/participant-manager/src/environments/environment.prod.ts), replace `<BASE_URL>` with your `participants.{PREFIX}-{ENV}.{DOMAIN}` value and `<auth-server-client-id>` with the value of your `auto-auth-server-client-id` secret (you can find this value in the [Secret Manager](https://console.cloud.google.com/security/secret-manager/) of your `{PREFIX}-{ENV}-secrets` project), for example:
         ```bash
         gcloud config set project $PREFIX-$ENV-secrets
         export auth_server_client_id=$( \
           gcloud secrets versions access latest --secret="auto-auth-server-client-id")
         sed -e 's/<BASE_URL>/participants.'$PREFIX'-'$ENV'.'$DOMAIN'/g' \
           -e 's/<AUTH_SERVER_CLIENT_ID>/'$auth_server_client_id'/g' -i.backup \
           $GIT_ROOT/participant-manager/src/environments/environment.prod.ts
         ```
    - Commit the changes to your repo, for example:
         ```bash
         cd $GIT_ROOT
         git checkout -b configure-application-properties
         git add $GIT_ROOT
         git commit -m "Initial configuration of application properties"
         git push origin configure-application-properties
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to build your container images, after which they will be available in the Container Registry of your apps project at `http://gcr.io/{PREFIX}-{ENV}-apps` (this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `{PREFIX}-{ENV}-apps` project)
1. Open [Secret Manager](https://console.cloud.google.com/security/secret-manager) for your `{PREFIX}-{ENV}-secrets` project and fill in the values for secrets with the prefix “manual-” (or set your `gcloud` project with `gcloud config set project $PREFIX-$ENV-secrets` and use the commands described below - afterwards clear your shell history with `history -c`)

    Manually set secret | Description | When to set | Example command
    --------------------------|-------------------|----------------------|-------------------
    `manual-mystudies-email-address` | The login of the email account you want MyStudies to use to send system-generated emails | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-email-address" --data-file=-`
    `manual-mystudies-email-password` | The password for that email account | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-email-password" --data-file=-`
    `manual-mystudies-contact-email-address` | The email address that the in-app contact and feedback forms will send messages to | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-contact-email-address" --data-file=-`
    `manual-mystudies-from-email-address` | The return email address that is shown is system-generated messages (for example, `no-reply@example.com`) This email should be an alias of `manual-mystudies-email-address`. Alternaitvely, provide the same email as `manual-mystudies-email-address` here as well | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-from-email-address" --data-file=-`
    `manual-mystudies-from-email-domain` | The domain of the above email address (just the value after “@”) | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-from-email-domain" --data-file=-`
    `manual-mystudies-smtp-hostname` | The hostname for your email account’s SMTP server (for example, `smtp.gmail.com`) | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-mystudies-smtp-hostname" --data-file=-`
    `manual-mystudies-smtp-use-ip-allowlist` | Typically ‘false’; if ‘true’, the platform will not authenticate to the email server and will rely on the allowlist configured in the SMTP service | Set this value to `true` or `false` now (you can update it later) | `echo -n "false" \| gcloud secrets versions add "manual-mystudies-smtp-use-ip-allowlist" --data-file=-`
    `manual-log-path` | The path to a directory within each application’s container where your logs will be written (for example `/logs`) | Set this value now | `echo -n "/logs" \| gcloud secrets versions add "manual-log-path" --data-file=-`
    `manual-org-name` | The name of your organization that is displayed to users, for example ‘Sincerely, the `manual-org-name` support team’ | Set this value now | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-org-name" --data-file=-`
    `manual-terms-url` | URL for a terms and conditions page that the applications will link to (for example, `https://example.com/terms`) | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-terms-url" --data-file=-`
    `manual-privacy-url` | URL for a privacy policy page that the applications will link to (for example, `https://example.com/privacy`) | Set this value now or enter a placeholder | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-privacy-url" --data-file=-`
    `manual-fcm-api-url` | [URL](https://firebase.google.com/docs/reference/fcm/rest) of your Firebase Cloud Messaging API ([documentation](https://firebase.google.com/docs/cloud-messaging/http-server-ref)) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [Android](/Android/) deployment (leave as placeholder if you will be deploying to iOS only) | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-fcm-api-url" --data-file=-`
    `manual-android-deeplink-url` | The sign-in screen is run on the Hydra-based auth server. This URL is a deep link that helps redirect users to the native mobile app after they sign in. (for example, `app://{PREFIX}-{ENV}.{DOMAIN}/mystudies`) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [Android](/Android/) deployment (leave as placeholder if you will be deploying to iOS only) | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-android-deeplink-url" --data-file=-`
    `manual-ios-deeplink-url` | The sign-in screen is run on the Hydra-based auth server. This URL is a deep link that helps redirect users to the native mobile app after they sign in. (for example, `app://{PREFIX}-{ENV}.{DOMAIN}/mystudies`) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [iOS](/iOS/) deployment (leave as placeholder if you will be deploying to Android only) | `echo -n "<SECRET_VALUE>" \| gcloud secrets versions add "manual-ios-deeplink-url" --data-file=-`
     > Note: When updating secrets after this initial deployment, you must refresh your Kubernetes cluster and restart the relevant pods to ensure the updated secrets are propagated to your applications (you do not need to do this now - only when making updates later), for example you can update your Kubernetes state with:
     ```bash
     cd $GIT_ROOT/deployment/terraform/kubernetes
     terraform init && terraform apply
     ```
    > then, restart the pods by deleting them in the Kubernetes dashboard or running:
     ```bash
     APP_PATH=<path_to_component_to_restart> # for example, $GIT_ROOT/auth-server
     kubectl scale --replicas=0 -f $APP_PATH/tf-deployment.yaml && \
     kubectl scale --replicas=1 -f $APP_PATH/tf-deployment.yaml
     ```
     > If you rotate an application’s ‘client_id’ or ‘client_secret’, such as `auto-response-datastore-client-id` or `auto-response-datastore-secret-key`, you must register the new values in Hydra by re-running the `register_clients_in_hydra.sh` script or executing the appropriate REST requests directly (see [`/hydra/README.md`](/hydra/README.md) for more information about working with Hydra manually)
1. Finish Kubernetes cluster configuration and deployment
    - Configure the remaining resources with Terraform, for example: 
         ```bash
         cd $GIT_ROOT/deployment/terraform/kubernetes/
         terraform init && terraform apply
         ```
    - Set your `kubectl` credentials, for example:
         ```bash
         gcloud container clusters get-credentials "$PREFIX-$ENV-gke-cluster" \
           --region=$LOCATION --project="$PREFIX-$ENV-apps"
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
    - Update firewalls:
        - Run `kubectl describe ingress $PREFIX-$ENV`
        - Look at the suggested commands under "Events", in the form of "Firewall
        change required by network admin"
        - Run each of the suggested commands
1. Verify the status of your Kubernetes cluster
     - Check the [Kubernetes ingress dashboard](https://console.cloud.google.com/kubernetes/ingresses) in your `{PREFIX}-{ENV}-apps` project to view the status of your cluster ingress (if status is not green, repeat the firewall step above)
    - Check the [Kubernetes workloads dashboard](https://console.cloud.google.com/kubernetes/workload) in your `{PREFIX}-{ENV}-apps` project to view the status of your applications (confirm all applications are green before proceeding - it can take up to 15 minutes for all containers to become operational)
    - [Check the status](https://cloud.google.com/load-balancing/docs/ssl-certificates/google-managed-certs#certificate-resource-status) of your Kubernetes cluster SSL certificates on the certificates page of your `{PREFIX}-{ENV}-apps` project’s [load balancing](https://console.cloud.google.com/net-services/loadbalancing/) ‘advanced menu’ (both the `participants.{PREFIX}-{ENV}.{DOMAIN}` and `studies.{PREFIX}-{ENV}.{DOMAIN}` certificates must be green for your deployment to use `https`) 
1. Configure your initial application credentials
    - Create the [`Hydra`](/hydra/) credentials for server-to-server requests by running [`register_clients_in_hydra.sh`](/deployment/scripts/register_clients_in_hydra.sh), for example:
         ```bash
         $GIT_ROOT/deployment/scripts/register_clients_in_hydra.sh \
           $PREFIX $ENV $DOMAIN
         ```
    - Create your first admin user account for the [`Participant manager`](/participant-manager/) application by running the [`create_participant_manager_superadmin.sh`](/deployment/scripts/create_participant_manager_superadmin.sh) script to generate and import a SQL dump file for the [`Participant datastore`](/participant-datastore/) database (the password you specify must be at least 8 characters long and contain lower case, upper case, numeric and special characters), for example:
         ```bash
         $GIT_ROOT/deployment/scripts/create_participant_manager_superadmin.sh \
           $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
         ```
    - Create your first admin user account for the [`Study builder`](/study-builder/) application by running the [`create_study_builder_superadmin.sh`](/deployment/scripts/create_study_builder_superadmin.sh) script to generate and import a SQL dump file for the [`Study datastore`](/study-datastore/) database, for example:
         ```bash
         sudo apt-get install apache2-utils -y
         $GIT_ROOT/deployment/scripts/create_study_builder_superadmin.sh \
           $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
         ```

### Configure your first study

1. Navigate your browser to `studies.{PREFIX}-{ENV}.{DOMAIN}/studybuilder/` (the trailing slash is necessary) and use the account credentials that you created with the `create_study_builder_superadmin.sh` script to log into the [`Study builder`](/study-builder/) user interface
1. Change your password, then create any additional administrative accounts that you might need
1. Create a new app record in the Apps section. Read more about creating and managing apps in the next section. 
1. Create a new study in the Studies section and associate it with the app you want it to appear in. 
1. Publish your study to propagate your study values to the other platform components.
1. Navigate your browser to `participants.{PREFIX}-{ENV}.{DOMAIN}/participant-manager/` (the trailing slash is necessary), then use the account credentials that you created with the `create_participant_manager_superadmin.sh` script to log into the [`Participant manager`](/participant-manager/) user interface (if the `Participant Manager` application fails to load, confirm you are using `https` - this deployment requires `https` to be fully operational)
1. You will be asked to change your password; afterwards you can create any additional administrative accounts that you might need
1. Confirm your new study is visible in the `Participant manager` interface

### Manage apps in the Study Builder 
1. You can use the `Apps` section in the Study Builder to create and manage multiple mobile apps running off a single deployment of the platform.
1. Start by creating a new app record by filling out the required fields.
1. Once an app record is created, studies can be mapped to it in the Studies section. 
1. To start testing an app, fill out additional required app properties and configurations in the Study Builder, and publish the app to propagate the app’s properties to other platform components that need them, using the `Publish App` action. If you are testing out a new version of an app that already exists, ensure you have retained current app version information in the Developer Configurations section at this point - do not replace it with new version information. 
1. Confirm your app is visible in the Participant Manager interface and test out your app.
1. Once the app is tested and ready to go to the app stores, update or finalize the app properties to correspond to the app store version of the app that will go live, and publish the latest values, again using the `Publish App` action. At this point, ensure that the app version information in the Developer Configurations section still retains the current version information and that the `Force upgrade` field is set to `No`, even if you are pushing out a new version of an existing app to the app stores.
1. Upload the app to the app stores for review and approval.
1. Once the app is approved in both the app stores and live, revisit the Study Builder and update the app version information in the Developer Configurations section to the latest app version information. Also, at this point, if you wish to enforce an app update, update the `Force upgrade` field to `Yes`. Use the `Publish App` action again for these changes to take effect. 
1. These steps will ensure that app users get prompted to update their apps to the new version when they open the existing apps on their device.
1. Also, once your app is live, mark the app as `distributed` in the Study Builder to prevent inadvertent changes to key configurations that drive your live app.

1. Barring these few key configurations, most other app properties can be updated after the app is live. 
1. Note: Any from email addresses that you configure in the app’s properties must be an [alias](https://support.google.com/mail/answer/22370) of the `manual-mystudies-email-address` that is configured in the Secret Manager as part of the platform deployment process. If an alias is not available, please use the same email here. [This Github issue](https://github.com/GoogleCloudPlatform/fda-mystudies/issues/4104) has additional detail on the alias requirements.

### Clean up

1. Remove your user account from the groups you no longer need access to
1. Revoke user access in your environment, for example:
    ```bash
    gcloud auth revoke <user>@<domain> -q && \
      gcloud auth application-default revoke -q
    ```
1. Optionally, confirm no other users are logged in, for example:
    ```bash
    gcloud auth list
    ``` 
## Maintaining & Updating FDA MyStudies

### Database Migration

When updating FDA MyStudies it may be necessary to migrate the databases to support the new version. Detailed instructions can be found in the [Database Migration README](/db-migration/README.md).

### Study Resources in Cloud Storage (2.0.5 upgrade)

Release 2.0.5 changed permissions for the Cloud Storage buckets used for study resources to remove public access and use signed URLs. New deployments will use this behavior automatically. When upgrading a prior release to 2.0.5 or greater, you will need to change permissions to the storage bucket using one of the following processes.

#### Manual process:
Go to Data Project ({prefix}-{env}-data) and remove `AllUser` access from the ({prefix}-{env}-mystudies-study-resources) storage bucket and provide access to the service accounts below with storage.objectAdmin role

*  serviceAccount:participant-manager-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com
*  serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com
*  serviceAccount:study-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com

#### Script process:
In the Terraform/{prefix}-{env}-data /main.tf file, please replace the existing `module "{prefix}_{env}_mystudies_study_resources>"` with the values below, replacing with your prefix and env values

```bash
module "{prefix}_{env}_mystudies_study_resources" {
  source = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name = "{prefix}-{env}-mystudies-study-resources"
  project_id = module.project.project_id
  location = "us-east1"

  iam_members = [
    {
      member = "serviceAccount:study-builder-gke-sa@{prefix}-{env}-apps.iam.gserviceaccount.com"
      role = "roles/storage.objectAdmin"
    },
    {
      member = "serviceAccount:study-datastore-gke-sa@{prefix}-{env}-apps.iam.gserviceaccount.com"
      role = "roles/storage.objectAdmin"
    },
    {
      member = "serviceAccount:participant-manager-gke-sa@{prefix}-{env}-apps.iam.gserviceaccount.com"
      role = "roles/storage.objectAdmin"
    },
  ]
}
```

#### tfengine process:

Pull the latest code (2.0.5+) and run the following commands

```bash
cd $GIT_ROOT
tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
git checkout -b bucket-permissions
git add $GIT_ROOT/deployment/terraform
git commit -m "bucket-permissions"
git push origin bucket-permissions
```

Then create a pull request from `bucket-permissions` branch to your target branch
Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to trigger terraform apply(you can view the status of the operation in the Cloud Build history of your devops project)

### Study Import / Export (2.0.6 upgrade)

Release 2.0.6 added additional functionality to support study import and export. This requires a permission change and an additional secret to be added. When upgrading a prior release to 2.0.6 or greater, you will need to perform the following steps.

#### Permission change to import bucket

Update your repository with the latest changes from release 2.0.6 or greater, create a new working branch and make the following changes:

1.  In the file `deployment/terraform/{prefix}-{env}-data/main.tf` find the section `module "{prefix}_{env}_mystudies_sql_import" { [...] }` and completely replace it with the following, substituting your values for `{prefix}` & `{env}` and changing the location to your preference:

    ```bash
    module "{prefix}-{env}_mystudies_sql_import" {
    source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
    version = "~> 1.4"

    name       = "{prefix}-{env}-mystudies-sql-import"
    project_id = module.project.project_id
    location   = "us-east1"

    iam_members = [
      {
        member = "serviceAccount:${module.mystudies.instance_service_account_email_address}"
        role   = "roles/storage.objectViewer"
      },
      {
        member = "serviceAccount:study-builder-gke-sa@{prefix}-{env}-apps.iam.gserviceaccount.com"
        role   = "roles/storage.objectAdmin"
      },
    ]
    }

    ```

1.  Create a pull request from this working branch to your specified branch, which will start the terraform plan and validation. After completion of the plan and validation, merge the pull request. That will run the terraform apply.

#### Add import / export bucket to Kubernetes cluster shared secrets

To add the bucket to the shared secrets, create a new working branch and make the following change:

1.  Edit the file `deployment/terraform/kubernetes/main.tf` and in the section `# Shared secrets` add the following line to the section `data = { [...] }`, substituting your values for `{prefix}` & `{env}`

    ```bash
    study_export_import_bucket_name   = "{prefix}_{env}-mystudies-sql-import" 
    ```

1.  Create a pull request from this working branch to your specified branch, which will start the terraform plan and validation. After completion of the plan and validation, merge the pull request. That will run the terraform apply.

1.  Pull the latest code from your repository and checkout your specified branch which contains the new shared secret.

1.  Run the following commands to apply the changes to your cluster:

    ```bash
    cd $GIT_ROOT/deployment/terraform/kubernetes/
    terraform init && terraform apply
    ```

1. Run the following command to apply the latest Study Builder deployment changes:

    ```bash
    kubectl apply \
      -f $GIT_ROOT/study-builder/tf-deployment.yaml
    ```

### Managing apps (2.0.8 upgrade)

Release 2.0.8 added functionality to support managing mobile apps in the deployment with the Study Builder interface. This requires that apps that are running in existing deployments must be updated (and new versions published to the app stores) if the deployment is being upgraded to 2.0.8 or greater. 

#### Required steps when upgrading to 2.0.8 or greater

When upgrading a prior release to 2.0.8 or greater, you will need to perform the following steps to continue to support existing apps.

1. Take the latest code and generate the mobile app build following the latest iOS and Android app build and deployment instructions given in the repo. Ensure you use the same App ID as before.
1. Sign in to the Study Builder and create an app record that has the exact same App ID that you have been using for your app. Ensure that you choose the correct app settings as well as applicable to your live app (gateway or standalone type of app, platform(s) that need to be supported etc.)
1. Cross-check if all the studies that belong to the app, are mapped to this app in their respective study creation sections.
1. In the newly created app record, fill out all the required app properties and configurations as applicable to a test version of the app and publish the app, using the `Publish App` action.  At this point, ensure you have retained current app version information in the Developer Configurations section - do not replace it with new version information. 
1. Confirm your app is still visible in the Participant Manager interface. Test out your newly generated app with the published configurations. 
1. Once the app is tested and ready to go to the app stores, update or finalize these app properties to correspond to the app store version of the app that will go live, and publish the latest values, again using the `Publish App` action. At this point, ensure that the app version information in the Developer Configurations section still retains the current version information and that the `Force upgrade` field is set to `No`.
1. Upload the app to the app stores for review and approval.
1. Once the app is approved in both the app stores and live, revisit the Study Builder and update the app version information in the Developer Configurations section to the latest app version information. Also, at this point, if you wish to enforce an app update, update the `Force upgrade` field to `Yes`. Use the `Publish App` action again for these changes to take effect. 
1. These steps will ensure that app users get prompted to update their apps to the new version when they open the existing apps on their device.
1. As a last step, once your app is live, mark the app as `distributed` in the Study Builder to prevent inadvertent changes to key configurations that drive your live app.


#### Changes to iOS push notifications in 2.0.8 or greater

This release uses Firebase Cloud Messaging (FCM) for push notifications for the iOS app. Follow the step `Configure Firebase Cloud Messaging (FCM) for push notifications` in the [iOS Configuration Instructions](/iOS/README.md#configuration-instructions) to set up FCM for iOS. Note that the server key generated here is to be entered into the developer configurations section of the app in the Study builder. 

#### Changes to secrets when upgrading to 2.0.8 or greater

The following secrets which were in earlier versions are no longer being used as of 2.0.8:
-   `manual-android-bundle-id`
-   `manual-android-server-key`
-   `manual-ios-bundle-id`
-   `manual-ios-certificate`
-   `manual-ios-certificate-password`
-   `manual-mobile-app-appid`

These secrets can be deleted from your deployment with the following steps. However, make sure you have a record of them handy before deleting, as these need to be updated in the Study Builder interface when [managing the apps](#manage-apps-in-the-study-builder)

1. Update your repository with the latest changes from release 2.0.8 or greater, create a new working branch and make the following changes:
1. In the file `deployment/terraform/{prefix}-{env}-secret/main.tf`, remove the following resources:
    -   ```
        resource "google_secret_manager_secret" "manual_mobile_app_appid" {
          [...]
        }
        ```
    -   ```
        resource "google_secret_manager_secret" "manual_android_bundle_id" {
          [...]
        }
        ```
    -   ```
        resource "google_secret_manager_secret" "manual_android_server_key" {
          [...]
        }
        ```
    -   ```
        resource "google_secret_manager_secret" "manual_ios_bundle_id" {
          [...]
        }
        ```
    -   ```
        resource "google_secret_manager_secret" "manual_ios_certificate" {
          [...]
        }
        ```
    -   ```
        resource "google_secret_manager_secret" "manual_ios_certificate_password" {
          [...]
        }
        ```
1.  Create a pull request from this working branch to your specified branch, which will start the terraform plan and validation. After completion of the plan and validation, merge the pull request. That will run the terraform apply.


***
<p align="center">Copyright 2020 Google LLC</p>
