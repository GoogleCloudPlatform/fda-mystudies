# FDA MyStudies Terraform Infrastructure

These directories define the entire GCP infrastructure app to run the Heroes Hat
application.

## Pre-Requisites

1. Install the following dependencies and add them to your PATH:
    - [GCloud](https://cloud.google.com/sdk/gcloud)
    - [Terraform](https://www.terraform.io/)
    - [Terragrunt](https://terragrunt.gruntwork.io/)

1. Get familiar with [GCP](https://cloud.google.com/docs/overview),
   [Terraform](https://www.terraform.io/intro/index.html) and
   [Terragrunt](https://blog.gruntwork.io/terragrunt-how-to-keep-your-terraform-code-dry-and-maintainable-f61ae06959d8).

   The infrastructure is deployed using Terraform, which is an industry standard
   for defining infrastructure-as-code. Terragrunt is used as a wrapper around
   Terraform to manage multiple Terraform deployments and reduce duplication.

1. Setup your [G Suite Domain](https://gsuite.google.com/).

1. Create admin groups:

   - X-org-admins@domain.com
   - X-devops-owners@domain.com
   - X-audit-owners@domain.com
   - X-apps-owners@domain.com
   - X-data-owners@domain.com
   - X-networks-owners@domain.com
   - X-firebase-owners@domain.com
   - X-auditors@domain.com

   NOTE: It is always recommended to use CICD to deploy changes to the
   infrastructure. These groups should remain empty and only have humans added
   for emergency break the glass situations or when debugging is required.
   Always join the group that grants you access to the fewest number of
   resources to do the job.


## Directory Structure

The infrastructure is split into multiple directories. Each directory represents
one Terraform deployment. Each deployment will manage specific resources in your infrastructure.

A deployment typically contains the following files:

- *main.tf*: This file defines the Terraform resources and modules to manage. For
  more complex deployments, there may be multiple .tf files that define
  resources.

- *variables.tf*: This file defines any input variables that the deployment can
  take.

- *outputs.tf*: This file defines any outputs from this deployment. These values
  can be used by other deployments.

- *terraform.tfvars*: This file defines values for the input variables.

- *terragrunt.hcl*: This file defines dependencies between other deployments,
  the remote state, and input values from other dependent deployments.

## Layout

|- bootstrap: one time setup to create projects to host Terraform state and CICD pipeline.

|- cicd: CloudBuild configs for the CICD pipeline.

|- org: org level resources.

  |- terragrunt.hcl: root Terragrunt config which defines remote state for all deployments.

  |- project.X-devops: additional resources that will go in the devops project.

  |- project.X-audit: the project to hold all audit logs for the org.

  |- audit: deployment to setup auditing for the org.

  |- iam: org level iam definitions such as org admins.

  |- folder.fda-my-studies: folder to hold all projects related to FDA MyStudies.

    |- project.X-apps: apps project and resources (GKE)

    |- project.X-data: data project and resources (GCS buckets, CloudSQL instances)

    |- project.X-networks: network project and resources (VPC)

    |- project.X-firebase: firebase project (firestores)
