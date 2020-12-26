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

Following this guide will result in your own unique instance of the FDA MyStudies platform. The resulting deployment will have the structure illustrated in **Figure 1**. Each functionally distinct aspect of the platform is deployed into its own cloud project to facilitate compartmentalization and robust access management. Each project and resource is named for its purpose, and has a `{PREFIX}-{ENV}` label, where `{PREFIX}` is a consistent name of your choice and `{ENV}` delineates your environment (for example, `dev` or `prod`). The list of projects you will create for your deployment is as follows:

Project | Name | Purpose
------------------------------------
DevOps | `{PREFIX}-{ENV}-devops` | tbd
Apps | `{PREFIX}-{ENV}-apps` | tbd
Data | `{PREFIX}-{ENV}-data` | tbd
Firebase | `{PREFIX}-{ENV}-firebase` | tbd
Networks | `{PREFIX}-{ENV}-networks` | tbd
Secrets | `{PREFIX}-{ENV}-secrets` | tbd
Audit | `{PREFIX}-{ENV}-audit` | tbd

The `{PREFIX}-{ENV}-apps` project is where the various FDA MyStudies applications run. **Figure 2** diagrams each the applications and how they related to their data sources. This deployment configures the applications URLs as follows:
Application | URL | Notes
------------------------------------
[Study builder](/study-builder/) | `studies.{PREFIX}.{DOMAIN}/study-builder` | This URL navigates an administrative user to the `Study builder` user interface
[Study datastore](/study-datastore/) | `studies.{PREFIX}.{DOMAIN}/study-datastore` | This URL is for the `Study datastore` back-end service
[Participant manager](/participant-manager/) | `participants.{PREFIX}.{DOMAIN}/participant-manager` | This URL navigates an administrative user to the `Participant manager` user interface
[Participant manager datastore](/participant-manager-datastore/) | `participants.{PREFIX}.{DOMAIN}/participant-manager-datastore` | This URL is for the `Participant manager datastore` back-end service
[Participant datastore](/participant-datastore/) | `participants.{PREFIX}.{DOMAIN}/participant-user-datastore`<br/>`participants.{PREFIX}.{DOMAIN}/participant-enroll-datastore`<br/>`participants.{PREFIX}.{DOMAIN}/participant-consent-datastore` | These URLs are for the `Participant datastore` back-end services
[Response datastore](/response-datastore/) | `participants.{PREFIX}.{DOMAIN}/response-datastore` | This URL is for the `Response datastore` back-end service
[Auth server](/auth-server/) | `participants.{PREFIX}.{DOMAIN}/auth-server` | This URL is for the administrative users and study participants to log into their respective applications
[Hydra](/hydra/) | `participants.{PREFIX}.{DOMAIN}/oauth2` | This URL is used by the `Auth server` to complete OAuth 2.0 consent flows

More information about the purpose of each application can be found in the [*Platform Overview*](/documentation/architecture.md) guide. Detailed information about configuration and operation of each application can be found in their [respective READMEs](/documentation/README.md).

**Figure 1: Overall architecture of the semi-automated deployment**
![Architecture](/documentation/images/deployment-reference-architecture.svg "Architecture")

**Figure 2: Application architecture**
![Applications](/documentation/images/apps-reference-architecture.svg "Applications")

The deployment process takes the following approach:
1. Create a copy of the FDA MyStudies repository that you will use for your deployment
1. Create the ‘devops` cloud project that will be used to orchestrate your deployment
1. Connect your cloned FDA MyStudies repository to your `devops` project and set up CICD pipelines to automate the rest of your deployment
1. Provision the necessary cloud resources using your CICD pipelines
1. Set up a second CICD pipeline that will automate creation of your application containers
1. Create a Kubernetes cluster to run your application containers
1. Create your initial user accounts, set up the required certificates, secrets, URLs, policies and network mesh
1. Customize branding and text content as desired
1. Deploy your mobile applications

### Before you begin

1. Familiarize yourself with:
    -    [Terraform](https://www.terraform.io/) and [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine)
    -    [Kubernetes](https://kubernetes.io/) and [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs/how-to/cluster-access-for-kubectl) (GKE)
    -    [CICD](https://en.wikipedia.org/wiki/CI/CD) and [Google Cloud Build](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build)
    -    [IAM](https://en.wikipedia.org/wiki/Identity_management) and Google Cloud’s [resource hierarchy](https://cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy)
1. Understand how the Terraform config files are named and organized for the FDA MyStudies deployment:
    -  {PREFIX} and {ENV} explanation
    -  Overview of the different folders that get created and point to example files in the repo
    - Explanation of the URLs of the services
```bash
|- devops/: one time manual deployment to create projects to host Terraform state and CICD pipeline.
|- cicd/: one time manual deployment to create CICD pipelines and configure permissions.
|- audit/: audit project and resources (log bucket, dataset and sinks).
|- {PREFIX}-{ENV}-apps/: apps project and resources (GKE).
|- {PREFIX}-{ENV}-data/: data project and resources (GCS buckets, CloudSQL instances).
|- {PREFIX}-{ENV}-firebase/: firebase project and resources (firestores).
|- {PREFIX}-{ENV}-networks/: networks project and resources (VPC, bastion host).
|- {PREFIX}-{ENV}-secrets/: secrets project and resources (secrets).
|- kubernetes/: manual kubernetes deployment after the GKE cluster has been created.
```
Deployment-related resources can be found throughout the GitHub repository.
-   The  [`deployment/`](/deployment/) directory contains step-by-step deployment instructions (this document) and the [`deployment.hcl`](deployment.hcl) and [`mystudies.hcl`](mystudies.hcl) Terraform configuration files, which provide [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine) the recipe for the overall deployment.
-   The [`deployment/kubernetes/`](/deployment/kubernetes/) directory contains step-by-step instructions for configuring the MySQL database and the Kubernetes cluster created by Terraform
-   The others directories in this repository map to the various components of the **FDA MyStudies** platform and contain Terraform and Kubernetes configuration files, such as `tf-deployment.yaml` and `tf-service.yaml`, that support each component’s deployment

The generated Terraform configs from the template deploys the FDA MyStudies
infrastructure in a dedicated folder with remote Terraform state management and
CICD pipelines enabled by default. The generated Terraform configs should be
checked-in to a GitHub repository.
    - Each directory (devops/, cicd/, {PREFIX}-{ENV}-apps/, etc) represents one
Terraform “deployment”. Each deployment will manage specific resources in you
infrastructure. A deployment typically contains the following files:
        - **main.tf**: This file defines the Terraform resources and modules to
    manage.
        - **variables.tf**: This file defines any input variables that the deployment
    can take.
       - **outputs.tf**: This file defines any outputs from this deployment. These
    values can be used by other deployments.
      - **terraform.tfvars**: This file defines values for the input variables.
To see what resources each deployment provisions, check out the comments in both
[mystudies.hcl](./mystudies.hcl) file and individual **main.tf** file.

Deployments listed under `managed_modules` in the `cicd` recipe are configured
to be deployed via CICD pipelines.

### Prepare Google Cloud Platform

1. Make sure you have access to a Google Cloud environment that contains an [organization resource](https://cloud.google.com/resource-manager/docs/creating-managing-organization#acquiring) (if you haven’t already, you may need to [create](https://support.google.com/a/answer/9983832) a Google Workspace and select a domain)
1. Confirm that the billing account that you will be using has [quota](https://support.google.com/cloud/answer/6330231?hl=en) for 10 or more projects (newly created billing accounts may only support 3-5 projects)
    - You can test how many projects your billing account can support by manually [creating projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects) and [linking them](https://cloud.google.com/billing/docs/how-to/modify-project#enable_billing_for_a_project) to your billing account, if you are able to link 10 projects to your billing account then you can proceed, otherwise [request additional quota](https://support.google.com/code/contact/billing_quota_increase) (don’t forget to unlink your billing account from your test projects, otherwise your quota may be exhausted) 
1. [Create a folder](https://cloud.google.com/resource-manager/docs/creating-managing-folders) that you will deploy your FDA MyStudies infrastructure into (or have your Google Cloud administrator do this for you - the [`Folder Admin`](https://cloud.google.com/resource-manager/docs/access-control-folders) role for the organization is required)
1. Make sure you have access to a user account with the following Cloud IAM roles:
    - `roles/resourcemanager.folderAdmin` for the folder you created
    - `roles/resourcemanager.projectCreator` for the folder you created
    - `roles/compute.xpnAdmin` for the folder you created
    - `roles/billing.admin` for the billing account that you will use
1. [Create](https://support.google.com/a/answer/33343?hl=en) the following
    administrative [IAM](https://cloud.google.com/iam/docs/overview#concepts_related_identity) groups that will be used during deployment:
    - {PREFIX}-{ENV}-folder-admins@{DOMAIN}: Members of this group have the [resourcemanager.folderAdmin](https://cloud.google.com/iam/docs/understanding-roles#resource-manager-roles) role for your deployment’s folder (for example, a deployment with prefix "mystudies", environment “prod” and domain "example.com", would require a group named "mystudies-prod-folder-admins@example.com")
    - {PREFIX}-{ENV}-devops-owners@{DOMAIN}: Members of this group have owners access
        for the devops project, which is required to make changes to the CICD pipeline and Terraform state
    - {PREFIX}-{ENV}-auditors@{DOMAIN}: Members of this group have the [iam.securityReviewer](https://cloud.google.com/iam/docs/understanding-roles#iam-roles) role for your deployment’s folder, and the bigquery.user and storage.objectViewer roles for your audit log project
    - {PREFIX}-{ENV}-cicd-viewers@{DOMAIN}: Members of this group can view CICD results in Cloud Build, for example the results of the `terraform plan` presubmit and `terraform apply` postsubmit
    - {PREFIX}-{ENV}-bastion-accessors@{DOMAIN}: Members of this group have permission to access the [bastion host](https://cloud.google.com/solutions/connecting-securely#bastion) project, which provides access to the private Cloud SQL instance


### Set up your environment
