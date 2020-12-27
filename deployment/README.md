<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

![FDA MyStudies](documentation/images/MyStudies_banner.svg "FDA MyStudies") 

## Overview

The FDA’s MyStudies platform enables organizations to quickly build and deploy studies that interact with participants through purpose-built apps on iOS and Android. MyStudies apps can be distributed to participants privately or made available through the App Store and Google Play.

This open-source repository contains the code necessary to run a complete FDA MyStudies instance, inclusive of all web and mobile applications.

Open-source [deployment tools](deployment) are included for semi-automated deployment to Google Cloud Platform (GCP). These tools can be used to deploy the FDA MyStudies platform in just a few hours. These tools follow compliance guidelines to simplify the end-to-end compliance journey. Deployment to other platforms and on-premise systems can be performed manually.

![Platform Illustration](documentation/images/platform_illustration.png "Platform Illustration")

## Documentation and guides

Information related to the deployment and operation of FDA Mystudies can be found within each directory’s `README`, and also in the following guides:

* [High-level platform and repo overview](README.md) (this document)<!--TODO * [Feature and functionality demonstrations](documentation/demo.md)-->
* [Detailed platform architecture](documentation/architecture.md)
* [Instructions for semi-automated deployment](deployment/README.md)
<!-- TODO
* Quick-start guide for manual deployment(documentation/manual-quickstart.md
* User guides study builder, participant manager and mobile applications(documentation/user-guides.md)
* API reference(documentation/api-reference.md)
-->

For the complete list of FDA MyStudies documentation, visit [`documentation/README.md`](/documentation/README.md). 

## Platform components and repo organization

Component | Intended users | Purpose | Directories
----------------|----------------------|------------|----------------
Study builder | Researchers and clinicians | No-code user interface for authoring studies ([demo screens](documentation/images/study-builder-screens.png)) | [`study-builder/`](study-builder/)<br/>[`study-datastore/`](study-datastore/)
Participant manager | Study coordinators | No-code user interface to manage participant enrollment ([demo screens](documentation/images/participant-manager-screens.png)) | [`participant-manager/`](participant-manager/)<br/>[`participant-manager-datastore/`](participant-manager-datastore/)
Mobile applications | Study participants | Apps to discover, enroll and participate in studies ([demo screens](documentation/images/mobile-screens.png)) | [`iOS/`](iOS/)<br/>[`Android/`](Android/)
Response datastore | Researchers and analysts | Collects and stores participant response data for downstream analysis | [`response-datastore/`](response-datastore/)
Participant datastore | Platform component | Manages participant data such as contact information and consent forms | [`participant-datastore/`](participant-datastore/)
Auth | Platform component | Manages account creation, login, logout and resource requests | [`hydra/`](/hydra/)<br/>[`auth-server/`](/auth-server/)
Deployment | System administrators | Infrastructure-as-code to build and maintain platform | [`deployment/`](deployment/)

Each high-level directory contains a `README.md` and the necessary deployment configuration files.

For more information about the platform architecture, visit the [Architecture overview](documentation/architecture.md). An example of how this architecture can be deployed on Google Cloud is diagrammed below.

![Example architecture](documentation/images/apps-reference-architecture.svg "Example architecture")

## Data and compliance

FDA MyStudies is designed so that all data stays within the deploying organization’s environment (unless that organization chooses to export their data). Any identifiable data is stored separately from study and response data to help organizations minimize access to sensitive data.

The FDA MyStudies platform has been designed to support auditing requirements for compliance with 21 CFR Part 11, allowing the platform to be used for trials under Investigational New Drug (IND) oversight. If an organization chooses to run FDA MyStudies on Google Cloud, a variety of infrastructure options are available that support HIPAA and other compliance requirements. More information about compliance on Google Cloud and an up-to-date list of products covered under BAA can be found [here](https://cloud.google.com/security/compliance/hipaa/).

In addition to the platform itself, the open-source [deployment tools](deployment) are designed to assist organizations with their end-to-end compliance journey. Although achieving compliance is the responsibility of the deploying organization, these toolkits enable organizations to deploy FDA MyStudies in a way that helps meet compliance requirements. More details of the deployment patterns used by these automation tools can be found [here](https://cloud.google.com/solutions/architecture-hipaa-aligned-project). 

Google Cloud can support compliance with 21 CFR Part 11 regulations when using GCP services in a prescribed manner to handle related data and workloads. While Google has a cloud technology stack ready for many 21 CFR Part 11 compliant workloads, the ultimate compliance determination depends on configuration choices made by the deploying organization.

## Release notes

For a detailed list of changes to the FDA MyStudies codebase, see *[What’s new](/documentation/whats-new.md)*.

## Feedback

Feature requests and bug reports should be submitted as [Github Issues](https://github.com/GoogleCloudPlatform/fda-mystudies/issues). All feedback is greatly appreciated.

***
<p align="center">Copyright 2020 Google LLC</p>
What’s changed README

<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

## What’s changed?
The overall goals, compliance principles and functionality of this FDA MyStudies release are similar to previous releases. Notable changes from version [`2019.10`](https://github.com/PopMedNet-Team/FDA-My-Studies-Mobile-Application-System/tree/2019.10) of FDA MyStudies include:

## Functionality
*   Removed dependencies on the LabKey framework  
*   Added the [`Response datastore`](/response-datastore/) as a platform-agnostic service to handle study response storage and access
*   Added the [`Participant manager`](/participant-manager/) graphical user interface and [`Participant datastore`](/participant-datastore/) backend to manage participant enrollment
*   Added support for [OAuth 2.0](https://oauth.net/2/) and [OIDC](https://openid.net/connect/)
*   Added [templates](/deployment/) for semi-automated deployment
*   Added support for infrastructure-as-code and CICD
*   Upgraded the [`Android`](/Android/) application for compatibility with Android 10
*   Improved exception handling
*   Improved request and data validation
*   Introduced unit tests and test frameworks to the codebase

## Architecture
*   Migrated to a modular [container-based](/deployment/kubernetes/) architecture
*   Refactored and extended `mobileAppStudy-ResponseServer` to [`Response datastore`](/response-datastore/)
*   Refactored `UserReg-WS` to [`Participant datastore`](/participant-datastore/)
*   Refactored `WCP-WS` and `Resources-WCP` to [`Study datastore`](/study-datastore/)
*   Reduced code duplication by extracting [`Common modules`](/common-modules) that are used by all new services
*   Migrated restrictive open-source dependencies to alternatives with permissive licenses 
*   Removed dependencies that require commercial licenses
*   Simplified mobile application calls and moved some functionality server-side to reduce dependencies between services
*   Adjusted data storage based on usage and security requirements (for example, study and participant status storage and calls were moved to[ `Response datastore`](/response-datastore/); enrollment generation logic was moved to [`Participant datastore`](/participant-datastore/))
    
## Security
* Replaced hard-coded credentials with scripts that inject initial users into each component
* Fixed potential cross-site scripting vulnerabilities
* Added query binding to all existing queries to prevent SQL injection
* Enhanced auth throughout codebase:
   * Unified distributed auth implementation into a single [`Auth server`](/auth-server/) (`Study builder` retains built-in auth)
   * Integrated with [Hydra](https://ory.sh/hydra), an OAuth 2.0 and OpenID Connect provider for OAuth 2.0 Access & Refresh token generation and authentication
   * Improved remaining authentication (for example, removed `client_secret` from being transmitted in all calls)
* Created [deployment templates](/deployment/) that support security best practices, such as:
  * Automation of secret generation and handling:
    * Configured secrets to be generated and stored with a [Secret Manager](https://cloud.google.com/secret-manager/docs/overview) instance deployed in an isolated cloud project
    * Configured secret values to be transmitted automatically within the private Kubernetes cluster
  * Implementation of [centralized network control](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control):
    * Configured deployment to use a [VPC host project](https://cloud.google.com/vpc/docs/shared-vpc) to manage networks and subnets in a centralized way (enabling network administration to be separated from project administration)
    * Enabled resources in different projects to communicate securely with internal IPs
  * Separation of projects with the security principle of least privilege:
    * Configured dedicated projects for different purposes (secrets, networks, applications, audit) for management by teams with isolated permissions - for example, a centralized network team can administer the network without having access the secrets project)
  * Implementation of [external access limitations](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#limit-access):
    * Configured databases and VMs to be isolated from the internet with only internal IP addresses ([Private Google Access](https://cloud.google.com/vpc/docs/configure-private-google-access))
    * Established [bastion host](https://cloud.google.com/solutions/connecting-securely#external) for secure on-demand connections to private instances
  * Implementation of DevOps best practices:
    * Configured [Continuous Integration and Continuous Deployment](https://cloud.google.com/solutions/managing-infrastructure-as-code) (CICD) pipelines to automate Cloud resource deployment and minimize direct human access
  * [Delegation of responsibility](https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#groups-and-service-accounts) through groups and service accounts:
    * Configured deployment to assign IAM roles to groups and service accounts so that individuals obtain permissions through groups rather than direct IAM roles

## Usability
*   Made interactions more intuitive for participants using the [`Android`](/Android/) and [`iOS`](/iOS/) mobile applications
*   Updated text for clarity in user interfaces and messages throughout the platform
*   Moved hard-coded values to centralized configuration files to streamline platform customization 
*   Improved code readability to simplify usability and extensibility for developers
*   Added support for unit testing, linter and CICD
*   Added detailed documentation and deployment instructions
    
## Bug fixes
*   Fixed stability and usability bugs throughout the applications and platform

***
<p align="center">Copyright 2020 Google LLC</p>

Platform overview
Staged here: https://github.com/GoogleCloudPlatform/fda-mystudies/blob/user-guide-updates/documentation/architecture.md

<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Platform Overview

FDA MyStudies consists of several components that work together as a platform. These components include web-based UIs for building studies and enrolling participants, backend services for managing the flow of data, and mobile applications that participants use to discover, enroll and participate in studies.

This document describes the architecture of FDA MyStudies. It outlines the various platform components and how they work together.

## Architecture

![Applications diagram](images/apps-reference-architecture.svg)

The diagram above illustrates the various applications that comprise the FDA MyStudies platform. The Android and iOS mobile applications are not shown. The diagram below illustrates how these applications fit into a production deployment that considers security, devops and data governance.

![Deployment diagram](images/deployment-reference-architecture.svg)

## Terminology

Some of the terms used in this document include:

1.  *Participant*: A mobile app user is referred to as a participant when he/she enrolls into a study and is associated with a unique participant id. A single mobile app user can be associated with multiple studies and is a unique participant in each study.
1.  *Administrator*: Users of the `Study builder` UI and `Participant manager` UI are referred to as administrators. These administrators could be researchers, clinical coordinators, sponsor personnel or site investigators and staff. 
1.  *Study content*: All the content that is required to carry out a study, which could include study eligibility criteria, consent forms, questionnaires or response types.
1.  *Response data*: The responses provided by a participant to questionnaires and activities that are presented as part of a study.

## Platform components

The platform components are as follows:

-  Administrative interfaces
   1. [Study builder](/study-builder/) (UI) to create and configure studies
   1. [Participant manager](/participant-manager) (UI) to enroll sites and participants
-  Security and access control
   1. [Hydra](/hydra/) for token management and OAuth 2.0
   1. [Auth server](/auth-server/) for login and credentials management
-  Data management
   1.  [Study datastore](/study-datastore/) to manage study configuration data
   1.  [Participant manager datastore](/participant-manager-datastore/) to process enrollment and consents
   1.  [Participant datastore](/participant-datastore/) to manage sensitive participant data
   1.  [Response datastore](/response-datastore/) to manage pseudonymized study responses
-  Participant interfaces
   1.  [Android](/Android/) mobile application (UI) to join and participate in studies
   1.  [iOS](/iOS/) mobile application (UI) to join and participate in studies 

Each of the components runs in its own Docker container. Blob storage, relational databases and a document store provide data management capabilities. Centralized logging enables auditing, and identity and access control compartmentalizes the flow of data. The specific technologies used to fulfil these roles is up to the deploying organization, but in the interest of simplicity, these guides describe an implementation that leverages Google Cloud Platform services. The [deployment guide](/deployment/) and individual component [READMEs](/documentation/) provide detailed instructions for how to set up and run the platform using these services. You might use one or more of the following cloud technologies:
- Container deployment
  -  [Kubernetes Engine](https://cloud.google.com/kubernetes-engine) (the Kubernetes approach to deployment is described in the automated [deployment guide](/deployment/))
  - [Compute Engine](https://cloud.google.com/compute) (the VM approach to deployment is described in the individual component [READMEs](/documentation/))
- Blob storage
  - [Cloud Storage](https://cloud.google.com/storage) buckets for (1) study content and (2) participant consent forms
- Relational database
  - [Cloud SQL](https://cloud.google.com/sql/) databases for (1) study configuration data, (2) sensitive participant data, (3) pseudonymized participant activity data, (4) Hydra client data and (5) user account credentials  
- Document store
  -  [Cloud Firestore](https://cloud.google.com/firestore) for pseudonymized participant response data
- Audit logging
  -  [Operations Logging](https://cloud.google.com/logging) for audit log writing and subsequent analysis
- Identity and access management
  - [Cloud IAM](https://cloud.google.com/iam) to create and manage service accounts and role-based access to individual resources
- Networking
  -  [Cloud DNS](https://cloud.google.com/dns) to manage domains
  -  [Virtual Private Cloud](https://cloud.google.com/vpc) to control ingress 
- Devops
  -  [Secret Manager](https://cloud.google.com/secret-manager) for generation, rotation and distribution of secrets
  -  [Cloud Build](https://cloud.google.com/cloud-build) for CI/CD
  -  [Container Registry](https://cloud.google.com/container-registry) for management of container images

Detailed information about the components and instructions for configuration can be found the README of [each directory](/documentation/). An explanation of how the platform components relate to one another is provided below.

### Study configuration

The [`Study builder`](/study-builder/) application provides a user interface for study administrators to create and launch studies and to manage study content during the course of a study. It does not handle any patient or participant information. It only deals with study content and configuration.


The `Study builder` is the source of study configuration for all downstream applications. As an administrator uses the UI to author their study, that study configuration data is written to a MySQL database that is shared with the [`Study datastore`](/study-datastore/). Once the administrator publishes their study, the `Study builder` notifies the [`Participant datastore`](/participant-datastore/) and [`Response datastore`](/response-datastore/) that new study information is available. Those datastores then retrieve the updated study configuration data from the `Study datastore`. When study administrators upload binary files to the `Study builder`, such as PDF documents or study images, those files are stored in blob storage. The participant mobile applications retrieve study configuration data from the `Study datastore` and the necessary binaries from blob storage directly. The `Study builder` uses built-in authorization and sends emails to study administrators for account creation and recovery purposes.

### Participant enrollment 

The [`Participant manager`](/participant-manager/) application provides a user interface for study administrators to create study sites and invite participants to participate in specific studies. The [`Participant manager datastore`](/participant-manager-datastore/) is the backend component of the `Participant manager` UI. The `Participant manager datastore` shares a MySQL database with the `Participant datastore`. As administrators use the UI to modify sites and manage participants, changes are propagated to the `Participant datastore` through the shared database.


When a new participant is added using the `Participant manager`, the `Participant manager datastore` sends an email to the participant with a link that can be used to enroll in the study. In the case of an *open enrollment* study, participants will be able to discover and join studies without a specific invitation. The participant goes to the mobile application to create an account, which uses the [`Auth server`](/auth-server/) to provide the necessary backend services. The `Auth server` sends the request for account creation to the `Participant datastore` to confirm that there is a study associated with that mobile application, and if confirmed, the `Auth server` validates the participant’s email and creates the account.


The mobile application populates the list of available studies by making requests to the `Study datastore`. When a participant selects a study to join, the mobile application retrieves the study eligibility questionnaire from the `Study datastore`. In the case where the participant was invited using the `Participant manager`, the mobile application confirms the invitation is valid with the `Participant datastore`. Once the `Participant datastore` determines that the participant is eligible for the study, the mobile application retrieves the study’s consent form from the `Study datastore`. After completion, the mobile application sends the consent form to the `Participant datastore`, which writes the consent PDF to blob storage. The participant is then enrolled in the study and a record is created for them in both the `Participant datastore` and `Response datastore`.

### Ongoing participation

The mobile application retrieves the list of study activities and the study schedule from the `Study datastore`. The mobile application posts updates to the `Response datastore` as participants start, pause, resume or complete study activities. The `Response datastore` writes this study activity data to its MySQL database. When the participant completes a study activity, the mobile application posts the results of that activity to the `Response datastore`, which writes that response data to Cloud Firestore.


If a participant sends a message with the mobile application’s contact form, that message is posted to the `Participant datastore`, which then sends an email to the configured destination. The `Participant datastore` can send participation reminders or other types of notifications to study participants through the mobile applications. When participants navigate to the dashboarding section of the mobile application, the mobile application will make a request to the `Response datastore` for the necessary study responses that are used to populate the configured dashboard. 

## Deployment and operation

Detailed deployment instructions can be found in the [deployment guide](/deployment/) and in each of the [directory READMEs](/documentation/).

***
<p align="center">Copyright 2020 Google LLC</p>

Deployment README

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

Following this guide will result in your own unique instance of the FDA MyStudies platform. The resulting deployment will have the structure illustrated in [**Figure 1**](#figure-1-overall-architecture-of-the-semi-automated-deployment). Each functionally distinct aspect of the platform is deployed into its own cloud project to facilitate compartmentalization and robust access management. Each project and resource is named for its purpose, and has a `{PREFIX}-{ENV}` label, where `{PREFIX}` is a consistent name of your choice and `{ENV}` delineates your environment (for example, `dev` or `prod`). The list of projects you will create for your deployment is as follows:

Project | Name | Purpose
---------|------------|---------------
Devops | `{PREFIX}-{ENV}-devops` | This project executes the Terraform CICD pipeline that keeps your infrastructure aligned with the state defined in the [`deployment/terraform/`](/deployment/terraform/) directory of your GitHub repository
Apps | `{PREFIX}-{ENV}-apps` | This project stores the container images for each of your FDA MyStudies applications, updates those images with CICD pipelines that monitor changes you make to the application directories of your GitHub repository, and administers the Kubernetes cluster that operates those images ([**Figure 2**](#figure-2-application-architecture) diagrams each the applications and how they related to their data sources)
Data | `{PREFIX}-{ENV}-data` | This project contains the MySQL databases that support each of the FDA MyStudies applications, and the blob storage buckets that hold study resources and consent documents
Firebase | `{PREFIX}-{ENV}-firebase` | This project contains the NoSQL database that stores the study response data
Networks | `{PREFIX}-{ENV}-networks` | This project administers the DNS and manages network ingress 
Secrets | `{PREFIX}-{ENV}-secrets` | This project manages the deployment’s secrets, such as client ids and client secrets
Audit | `{PREFIX}-{ENV}-audit` | This project stores the audit logs for the FDA MyStudies platform and applications

This deployment configures the applications URLs as follows:

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
1. Create your first study

### Before you begin

1. Familiarize yourself with:
    -    [Terraform](https://www.terraform.io/) and [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine)
    -    [Kubernetes](https://kubernetes.io/) and [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine/docs/how-to/cluster-access-for-kubectl)
    -    [CICD](https://en.wikipedia.org/wiki/CI/CD) and [Google Cloud Build](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build)
    -    [IAM](https://en.wikipedia.org/wiki/Identity_management) and Google Cloud’s [resource hierarchy](https://cloud.google.com/resource-manager/docs/cloud-platform-resource-hierarchy)
1. Understand how the Terraform config files and cloud resources are named and organized in the deployment:
    -  `{PREFIX}` is a name you choose for your deployment that will be prepended to various directories, cloud resources and URLs (for example this could be `mystudies`)
    -  `{ENV}` is a label you choose that will be appended to `{PREFIX}` in your directories and cloud resources (for example this could be `dev`, `test` or `prod`)
    - `{DOMAIN}` is the domain you will be using for your URLs (for example, `your_company_name.com` or `your_medical_center.edu`)
    - [`deployment/deployment.hcl`](/deployment/deployment.hcl) is the file where you will specify top-level parameters for your deployment (for example, the values of `{PREFIX}`, `{ENV}` and `{DOMAIN}`)
    - [`deployment/mystudies.hcl`](/deployment/mystudies.hcl) is the file that represents the overall recipe for the deployment (you will uncomment various aspects of this recipe as your deployment progresses)
    - The directories created in [`/deployment/terraform/`](/deployment/terraform/) by the `tfengine` command represent distinct cloud projects that the CICD pipeline monitors to create, update or destroy resources based on the changes you make to those directories
    -  The other directories in the FDA MyStudies repository map to the various components of the platform and contain Terraform and Kubernetes configuration files, such as `tf-deployment.yaml` and `tf-service.yaml`, that support each component’s deployment

### Prepare the cloud platform

1. Make sure you have access to a Google Cloud environment that contains an [organization resource](https://cloud.google.com/resource-manager/docs/creating-managing-organization#acquiring) (if you don’t have an organization resource, you can obtain one by [creating](https://support.google.com/a/answer/9983832) a Google Workspace and selecting a domain)
1. Confirm the billing account that you will use has [quota](https://support.google.com/cloud/answer/6330231?hl=en) for 10 or more projects (newly created billing accounts may default to a 3-5 project quota)
    - You can test how many projects your billing account can support by manually [creating projects](https://cloud.google.com/resource-manager/docs/creating-managing-projects) and [linking them](https://cloud.google.com/billing/docs/how-to/modify-project#enable_billing_for_a_project) to your billing account, if you are able to link 10 projects to your billing account then you can proceed, otherwise [request additional quota](https://support.google.com/code/contact/billing_quota_increase) (don’t forget to unlink the test projects from your billing account, otherwise your quota may be exhausted) 
1. [Create a folder](https://cloud.google.com/resource-manager/docs/creating-managing-folders) to deploy your FDA MyStudies infrastructure into (or have your Google Cloud administrator do this for you - the [`resourcemanager.folderAdmin`](https://cloud.google.com/resource-manager/docs/access-control-folders) role for the organization is required)
1. Confirm you have access to a user account with the following Cloud IAM roles:
    - `roles/resourcemanager.folderAdmin` for the folder you created
    - `roles/resourcemanager.projectCreator` for the folder you created
    - `roles/compute.xpnAdmin` for the folder you created
    - `roles/billing.admin` for the billing account that you will use
1. [Create](https://support.google.com/a/answer/33343?hl=en) the following
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

1. You can work in an existing environment, or you can configure a new environment by [creating](https://cloud.google.com/compute/docs/instances/create-start-instance) a VM instance in the Google Cloud project of your choice
1. Confirm you have the following dependencies installed and added to your `$PATH`:
    - [Install](https://cloud.google.com/sdk/docs/install) the Google Cloud command line tool `gcloud` (already installed if using a Google Compute Engine VM), for example:
         ```bash
         apt-get install google-cloud-sdk
         ```
    - [Install](https://cloud.google.com/storage/docs/gsutil_install) the Cloud Storage command line tool `gsutil` (already installed if using a Google Compute Engine VM)
    - [Install](https://kubernetes.io/docs/tasks/tools/install-kubectl) the Kubernetes command line tool `kubectl`, for example:
         ```bash
         sudo apt-get update && sudo apt-get install -y apt-transport-https gnupg2 curl
         curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
         echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
         sudo apt-get update
         sudo apt-get install -y kubectl
         ```
    - Install [Terraform 0.12.29](https://learn.hashicorp.com/tutorials/terraform/install-cli), for example:
         ```shell
         sudo apt-get install software-properties-common
         curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
         sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
         sudo apt-get update && sudo apt-get install terraform=0.12.29
         ```
    - Install [Go 1.14+](https://golang.org/doc/install), for example:
         ```shell
         sudo apt install wget
         wget https://golang.org/dl/go1.15.6.linux-amd64.tar.gz
         sudo tar -C /usr/local -xzf go1.15.6.linux-amd64.tar.gz
         export PATH=$PATH:/usr/local/go/bin
         ```
    - Install [Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine#installation), for example:
         ```shell
         VERSION=v0.4.0
         sudo wget -O /usr/local/bin/tfengine                             
         https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/releases/download/${VERSION}/tfengine_${VERSION}_linux-amd64
         sudo chmod +x /usr/local/bin/tfengine
         ```
1. [Duplicate](https://docs.github.com/en/free-pro-team@latest/github/creating-cloning-and-archiving-repositories/duplicating-a-repository) the [FDA MyStudies repository](https://github.com/GoogleCloudPlatform/fda-mystudies), then clone it locally
1. Update [`/deployment/deployment.hcl`](/deployment/deployment.hcl) with the values for your deployment
1. Update [`/deployment/scripts/set_env_var.sh`](/deployment/scripts/set_env_var.sh) for your deployment, then use the script to set your environment variables, for example:
    ```
    . ./set_env_var.sh # Executed from the deployment/scripts directory
    ```
1. Authenticate as a user with the permissions described above (this deployment assumes gcloud and Terraform commands are made as a user, rather than a service account)
    - Update your [application default credentials](https://cloud.google.com/docs/authentication/production), for example you could run `gcloud auth application-default login` (when using a Google Compute Engine VM you must update the application default credentials, otherwise requests will continue to be made with its default service account)
    - Remember to run `gcloud auth revoke` to log your user account out once your deployment is complete

### Set up your CICD pipelines

1. Generate your Terraform configuration files
    - Set the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl) to `false`
    - Execute the `tfengine` command to generate the configs (by default, CICD
    will look for Terraform configs under the `deployment/terraform/` directory in the
    GitHub repo, so set the `--output_path` to point to the `deployment/terraform/`
    directory inside the local root of your GitHub repository), for example:
         ```bash
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         ```
1. Use Terraform to create the `devops` project and Terraform state bucket (if this step fails, confirm you have updated your application default credentials and that the required version of Terraform is installed), for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init
    terraform apply
    ```
1. Backup the state of the `devops` project to the newly created state bucket by setting the `enable_gcs_backend` flag in [`mystudies.hcl`](/deployment/mystudies.hcl) to `true` and regenerating the Terraform configs, for example:
    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
    cd $GIT_ROOT/deployment/terraform/devops
    terraform init -force-copy
    ```
1. Open [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your new `devops` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers as Terraform will create them in the next step)
1. Create the CICD pipeline for your deployment (this will create the Cloud Builder triggers that will run whenever a pull request containing changes to files in `$GIT_ROOT/deployment/terraform/` is raised against the GitHub branch that you specified in [`deployment.hcl`](/deployment/deployment.hcl)), for example:
    ```bash
    cd $GIT_ROOT/deployment/terraform/cicd
    terraform init
    terraform apply
    ```
### Deploy your platform infrastructure

1. Commit your local git working directory (which now represents your desired infrastructure state) to a new branch in your cloned FDA MyStudies repository, for example using:
    ```bash
    git checkout -b initial-deployment
    git add $GIT_ROOT/deployment/terraform
    git commit -m "Perform initial deployment"
    git push origin initial-deployment
    ```
1. Trigger Cloud Build to run the Terraform pre-submit checks by using this new branch to [create](https://docs.github.com/en/free-pro-team@latest/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request) a pull request against the branch you specified in [`deployment.hcl`](/deployment/deployment.hcl) (you can view the status of your pre-submit checks and re-run jobs as necessary in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Once your pre-submit checks have completed successfully, and you have received code review approval, merge your pull request into the main branch to trigger the `terraform apply` post-submit operation (this operation may take up to 45 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)

    > Note: If your pre-submit checks or post-submit `terraform apply` fail with an error related to billing accounts, you may not have the [quota](https://support.google.com/cloud/answer/6330231?hl=en) necessary to attach all of your projects to the specified billing account. You may need to [request additional quota](https://support.google.com/code/contact/billing_quota_increase).
1. [Grant](https://cloud.google.com/iam/docs/granting-changing-revoking-access) the [`roles/owner`](https://cloud.google.com/resource-manager/docs/access-control-proj#using_basic_roles) permission to the `{PREFIX}-{ENV}-project-owners@{DOMAIN}` group for each of your newly created projects (you may need your Google Cloud administrator to do this for you)  

### Configure your deployment’s databases

1. [Create](https://console.cloud.google.com/datastore/) a [*Native mode*](https://cloud.google.com/datastore/docs/firestore-or-datastore) Cloud Firestore database in your `{PREFIX}-{ENV}-firebase` project
1. Use Terraform and CICD to create Firestore indexes, a Cloud SQL instance, user accounts and IAM role bindings
    - Uncomment the blocks for steps 5.1 through 5.6 in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         git checkout -b database-configuration
         git add $GIT_ROOT/deployment/terraform
         git commit -m "Configure databases"
         git push origin database-configuration
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 20 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Configure the permissions of your SQL script bucket so that your Cloud SQL instance can import the necessary initialization scripts
    - Uncomment the blocks for Steps 6 in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
         tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/deployment/terraform
         git checkout -b sql-bucket-permissions
         git add $GIT_ROOT/deployment/terraform
         git commit -m "Set SQL bucket permissions"
         git push origin sql-bucket-permissions
         ```
    - Once your pull request pre-submits have completed successfully, and you have received code review approval, merge your pull request to trigger `terraform apply`(this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `devops` project)
1. Initialize your MySQL databases by importing SQL scripts
    - Upload the necessary SQL script files to the `{PREFIX}-{ENV}-mystudies-sql-import` storage bucket that you created during Terraform deployment, for example:
         ```bash
         gsutil cp \
         ${GIT_ROOT}/study-builder/sqlscript/* \
         ${GIT_ROOT}/response-datastore/sqlscript/mystudies_response_server_db_script.sql \
         ${GIT_ROOT}/participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql \
         ${GIT_ROOT}/participant-datastore/sqlscript/mystudies_participant_datastore_db_script.sql \
         ${GIT_ROOT}/hydra/sqlscript/create_hydra_db_script.sql \
         gs://${PREFIX}-${ENV}-mystudies-sql-import
         ```
    - Import the SQL scripts from cloud storage to your Cloud SQL instance, for example:
         ```bash
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/create_hydra_db_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/HPHC_My_Studies_DB_Create_Script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/procedures.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/version_info_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_response_server_db_script.sql -q
         gcloud sql import sql --project=${PREFIX}-${ENV}-data mystudies gs://${PREFIX}-${ENV}-mystudies-sql-import/mystudies_participant_datastore_db_script.sql -q
         ```
1. [Enable](https://console.cloud.google.com/marketplace/product/google/sqladmin.googleapis.com) the [Cloud SQL Admin API](https://cloud.google.com/sql/docs/mysql/admin-api) for your `{PREFIX}-{ENV}-apps` project

### Configure and deploy your applications

1. Make a [request](https://cloud.google.com/compute/quotas#requesting_additional_quota) to increase the [Global Compute Engine API Backend Services quota]((https://console.cloud.google.com/iam-admin/quotas/details;servicem=compute.googleapis.com;metricm=compute.googleapis.com%2Fbackend_services;limitIdm=1%2F%7Bproject%7D)) for your `{PREFIX}-{ENV}-apps` project to 20 (if it is not already set at, or beyond, this value)
1. Enable CICD for the application directories of your cloned GitHub repository so that changes you make to the application code will automatically build the application containers for your deployment
    - Enable [Cloud Build](https://console.cloud.google.com/cloud-build/triggers) in your `{PREFIX}-{ENV}-apps` project and [connect](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app) your cloned GitHub repository (skip adding triggers as Terraform will create them in the next step)
    - Uncomment  the Cloud Build triggers portion of the apps project in [`mystudies.hcl`](/deployment/mystudies.hcl), then regenerate the Terraform configs and commit the changes to your repo, for example:
         ```bash
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
         -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup \
         $GIT_ROOT/deployment/kubernetes/cert.yaml
         
         sed -e 's/<PREFIX>/'$PREFIX'/g' \
         -e 's/<ENV>/'$ENV'/g' \
         -e 's/<DOMAIN>/'$DOMAIN'/g' -i.backup \
         $GIT_ROOT/deployment/kubernetes/ingress.yaml
         ```
    - In [`/participant-manager/src/environments/environment.prod.ts`](/participant-manager/src/environments/environment.prod.ts), replace `<BASE_URL>` with your `participants.{PREFIX}.{DOMAIN}` value and `<auth-server-client-id>` with the value of your `auto-auth-server-client-id` secret (you can find this value in the [Secret Manager](https://console.cloud.google.com/security/secret-manager/) of your `{PREFIX}-{ENV}-secrets` project), for example:
         ```bash
         export auth_server_client_id=<YOUR_VALUE>
         sed -e 's/<BASE_URL>/participants.’$PREFIX’.'$DOMAIN’/g' \
         -e 's/<AUTH_SERVER_CLIENT_ID>/'$auth_server_client_id'/g' -i.backup \
         $GIT_ROOT/participant-manager/src/environments/environment.prod.ts
         ```
    - Commit the changes to your repo, for example:
         ```bash
         git checkout -b configure-application-properties
         git add $GIT_ROOT
         git commit -m "Initial configuration of application properties"
         git push origin configure-application-properties
         ```
    - Once your pull request pre-submit checks have completed successfully, and you have received code review approval, merge your pull request to build your container images, after which they will be available in the Container Registry of your apps project at `http://gcr.io/{PREFIX}-{ENV}-apps` (this may take up to 10 minutes - you can view the status of the operation in the [Cloud Build history](https://console.cloud.google.com/cloud-build/builds) of your `{PREFIX}-{ENV}-apps` project)
1. Open [Secret Manager](https://console.cloud.google.com/security/secret-manager) for your `{PREFIX}-{ENV}-secrets` project and fill in the values for the secrets with prefix “manual-”:

    Manually set secret | Description | When to set
    --------------------------|-------------------|----------------------
    `manual-mystudies-email-address` | The login of the email account you want MyStudies to use to send system-generated emails | Set this value now or enter a placeholder
    `manual-mystudies-email-password` | The password for that email account | Set this value now or enter a placeholder
    `manual-mystudies-contact-email-address` | The email address that the in-app contact and feedback forms will send messages to | Set this value now or enter a placeholder
    `manual-mystudies-from-email-address` | The return email address that is shown is system-generated messages (you may want to use a no-reply@ address) | Set this value now or enter a placeholder
    `manual-mystudies-from-email-domain` | The domain of the above email address (just the value after “@”) | Set this value now or enter a placeholder
    `manual-mystudies-smtp-hostname` | The hostname for your email account’s SMTP server (for example, smtp.gmail.com) | Set this value now or enter a placeholder
    `manual-mystudies-smtp-use-ip-allowlist` | Typically ‘False’; if ‘True’, the platform will not authenticate to the email server and will rely on the allowlist configured in the SMTP service | Set this value now or enter a placeholder
    `manual-log-path` | The path to a directory within each application’s container where your logs will be written (for example `/logs`) | Set this value now
    `manual-org-name` | The name of your organization that is displayed to users, for example ‘Sincerely, the <manual-org-name> support team’ | Set this value now
    `manual-terms-url` | URL for a terms and conditions page that the applications will link to | Set this value now or enter a placeholder
    `manual-privacy-url` | URL for a privacy policy page that the applications will link to | Set this value now or enter a placeholder
    `manual-mobile-app-appid` | The value of the `App ID` that you will configure on the Settings page of the [Study builder](/study-builder/) user interface when you create your first study (you will also use this same value when configuring your mobile applications for deployment) | Set now if you know what value you will use when you create your first study - otherwise enter a placeholder and update once you have created a study in the [Study builder](/study-builder)
    `manual-android-bundle-id` | The value of `applicationId` that you will configure in [`Android/app/build.gradle`](/Android/app/build.gradle) during [Android configuration](/Android/) | If you know what value you will use during [Android](/Android/) deployment you can set this now, otherwise enter a placeholder and update later (leave as placeholder if you will be deploying to iOS only)
    `manual-fcm-api-url` | URL of your Firebase Cloud Messaging API ([documentation](https://firebase.google.com/docs/cloud-messaging/http-server-ref)) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [Android](/Android/) deployment (leave as placeholder if you will be deploying to iOS only)
    `manual-android-server-key` | The Firebase Cloud Messaging server key that you will obtain during [Android configuration](/Android/) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [Android](/Android/) deployment (leave as placeholder if you will be deploying to iOS only)
    `manual-android-deeplink-url` | The URL to redirect to after Android login (for example, `app://{PREFIX}.{DOMAIN}/mystudies`) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [Android](/Android/) deployment (leave as placeholder if you will be deploying to iOS only)
    `manual-ios-bundle-id` | The value you will obtain from Xcode (Project target > General tab > Identity section > Bundle identifier) during [iOS configuration](/iOS/) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [iOS](/iOS/) deployment (leave as placeholder if you will be deploying to Android only)
    `manual-ios-certificate` | The value of the Base64 converted `.p12` file that you will obtain during [iOS configuration](/iOS/) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [iOS](/iOS/) deployment (leave as placeholder if you will be deploying to Android only)
    `manual-ios-certificate-password` | The value of the password for the `.p12` certificate (necessary if your certificate is encrypted - otherwise leave empty) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [iOS](/iOS/) deployment (leave as placeholder if you will be deploying to Android only)
    `manual-ios-deeplink-url` | The URL to redirect to after iOS login (for example, `app://{PREFIX}.{DOMAIN}/mystudies`) | Set now if you know what this value will be - otherwise create a placeholder and update after completing your [iOS](/iOS/) deployment (leave as placeholder if you will be deploying to Android only)

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
        change required by network admin: <gcloud command>"
        - Run each of the suggested commands
1. Check the [Kubernetes dashboard](https://console.cloud.google.com/kubernetes/workload) in your `{PREFIX}-{ENV}-apps` project to view the status of your deployment
1. Configure your initial application credentials
    - Create the [`Hydra`](/hydra/) credentials for server-to-server requests by running [`register_clients_in_hydra.sh`](/deployment/scripts/register_clients_in_hydra.sh), for example:
         ```bash
         $GIT_ROOT/deployment/scripts/register_clients_in_hydra.sh \
         $PREFIX $ENV https://participants.$DOMAIN
         ```
    - Create your first admin user account for the [`Participant manager`](/participant-manager/) application by running the [`create_participant_manager_superadmin.sh`](/deployment/scripts/create_participant_manager_superadmin.sh) script to generate and import a SQL dump file for the [`Participant datastore`](/participant-datastore/) database, for example:
         ```bash
         $GIT_ROOT/deployment/scripts/create_participant_manager_superadmin.sh \
         $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
         ```
    - Create your first admin user account for the [`Study builder`](/study-builder/) application by running the [`create_study_builder_superadmin.sh`](/deployment/scripts/create_study_builder_superadmin.sh) script to generate and import a SQL dump file for the [`Study datastore`](/study-datastore/) database, for example:
         ```bash
         $GIT_ROOT/deployment/scripts/create_study_builder_superadmin.sh \
         $PREFIX $ENV <YOUR_DESIRED_LOGIN_EMAIL> <YOUR_DESIRED_PASSWORD>
         ```

### Configure your first study

1. Navigate your browser to `studies.{PREFIX}.{DOMAIN}/study-builder` and use the account credentials that you created with the `create_study_builder_superadmin.sh` script to log into the [`Study builder`](/study-builder/) user interface
1. Change your password, then create any additional administrative accounts that you might need
1. Create a new study with the `App ID` that you set in the `manual-mobile-app-appid` secret, or choose a new `App ID` that you will update `manual-mobile-app-appid` with
1. Publish your study to propagate your study values to the other platform components
1. Navigate your browser to `participants.{PREFIX}.{DOMAIN}/participant-manager` and use the account credentials that you created with the `create_participant_manager_superadmin.sh` script to log into the [`Participant manager`](/participant-manager/) user interface
1. Change your password, then create any additional administrative accounts that you might need
1. Confirm your new study is visible in the `Participant manager` interface

### Prepare your mobile applications

1. Follow the instructions in either or both [`Android`](/Android/) and [`iOS`](/iOS/) deployment guides (if you haven’t created a study yet, you can configure the mobile applications with the `APP_ID` you plan on using when you create your first study in the [`Study builder`](/study-builder/))
1. Open Secret Manager in your `{PREFIX}-{ENV}-secrets` project and update the secrets you previously configured with placeholder values (you can skip this step if you already configured your secrets with the appropriate values)
    - `manual-mobile-app-appid` is the value of the `App ID` that you configured, or will configure, on the Settings page of the [`Study builder`](/study-builder/)
    - `manual-android-bundle-id` is the value of `applicationId` that you configured in [`Android/app/build.gradle`](/Android/app/build.gradle)
    - `manual-fcm-api-url` is the URL of your Firebase Cloud Messaging API
    - `manual-android-server-key` is your Firebase Cloud Messaging server key
    - `manual-android-deeplink-url` is the URL to redirect to after Android login
    - `manual-ios-bundle-id` is the value you obtained from Xcode
    - `manual-ios-certificate` is the value of the Base64 converted `.p12` file
    - `manual-ios-certificate-password` is the value of the password for the `.p12` certificate 
    - `manual-ios-deeplink-url` is the URL to redirect to after iOS login
1. Initialize your `Participant datastore` database to work with your mobile applications
    - Once a study with the `App ID` corresponding to the `manual-mobile-app-appid` secret is created and published using the [`Study builder`](/study-builder) user interface, a corresponding 
app record will appear in the [`Participant manager`](/participant-manager/) user interface (if you created a study before all of your platform components were operational, you can reinitialize this process by using the `Study builder` user interface to pause and resume the study) 
    - Once the `App ID` appears in `Participant manager`, run the [deployment/scripts/copy_app_info_to_sql.sh](/deployment/scripts/copy_app_info_to_sql.sh) script to update the remaining databases, for example:
         ```bash
         $GIT_ROOT/deployment/scripts/copy_app_info_to_sql.sh $PREFIX $ENV
         ```

### Clean up

1. Remove your user account from the groups you no longer need access to
1. Revoke user access in your environment by running `gcloud auth revoke`

/*### Troubleshooting

See the [*Troubleshooting*](/documentation/troubleshooting.md) guide for more information.
*/
***
<p align="center">Copyright 2020 Google LLC</p>
