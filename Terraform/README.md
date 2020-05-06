# Deploy FDA MyStudies using Terraform

These directories define the entire infrastructure necessary to run FDA MyStudies on Google Cloud.

This Terraform deployment is an adaptation of Google Cloud's [HIPAA-aligned architecture](https://cloud.google.com/solutions/architecture-hipaa-aligned-project). This approach to project 
configuration and deployment is explained in the ["Setting up a HIPAA-aligned project"](https://cloud.google.com/solutions/setting-up-a-hipaa-aligned-project) solution guide.

This document provides instructions for deploying FDA MyStudies on Google Cloud in using
infrastrucutre-as-code in approximately 1 hour. A video tutorial that walks the user through
these steps is available upon request.

## Prerequisites

1.  Install the following dependencies and add them to your PATH:

    -   [GCloud](https://cloud.google.com/sdk/gcloud)
    -   [Terraform](https://www.terraform.io/)
    -   [Terragrunt](https://terragrunt.gruntwork.io/)

1.  Get familiar with [GCP](https://cloud.google.com/docs/overview),
    [Terraform](https://www.terraform.io/intro/index.html) and
    [Terragrunt](https://blog.gruntwork.io/terragrunt-how-to-keep-your-terraform-code-dry-and-maintainable-f61ae06959d8).

    The infrastructure is deployed using Terraform, which is an industry
    standard for defining infrastructure-as-code. Terragrunt is used as a
    wrapper around Terraform to manage multiple Terraform deployments and reduce
    duplication.

1.  Setup your
    [organization](https://cloud.google.com/resource-manager/docs/creating-managing-organization)
    for GCP resources and [G Suite Domain](https://gsuite.google.com/) for
    groups.

1.  [Create administrative groups](https://support.google.com/a/answer/33343?hl=en)
    in the G Suite Domain:

    -   {PREFIX}-org-admins@{DOMAIN}.com: This group has administrative access
        to the entire org. This group can be used in break-glass situations to
        give humans access to the org to make changes.

    -   {PREFIX}-devops-owners@{DOMAIN}.com: This group has owners access to the
        devops project to make changes to the CICD project or make changes to
        the Terraform state.

    -   {PREFIX}-auditors@{DOMAIN}.com: This group has security reviewer
        (metadata viewer) access to the entire org, as well as viewer access to
        the audit logs BigQuery and Cloud Storage resources.

    WARNING: It is always recommended to use CICD to deploy changes to the
    infrastructure. The groups above should remain empty and only have humans
    added for emergency situations or when investigation is required.

## Directory Structure

The infrastructure is split into multiple directories. Each directory represents
one Terraform deployment. Each deployment will manage specific resources in you
infrastructure.

A deployment typically contains the following files:

-   **main.tf**: This file defines the Terraform resources and modules to
    manage. For more complex deployments, there may be multiple .tf files that
    define resources.

-   **variables.tf**: This file defines any input variables that the deployment
    can take.

-   **outputs.tf**: This file defines any outputs from this deployment. These
    values can be used by other deployments.

-   **terraform.tfvars**: This file defines values for the input variables.

-   **terragrunt.hcl**: This file defines dependencies between other
    deployments, the remote state, and input values from other dependent
    deployments.

To see what resources each deployment provisions, check out the comments in each
**main.tf** file.

## Layout

```
|- bootstrap: one time setup to create projects to host Terraform state and CICD pipeline.
|- cicd: CloudBuild configs for the CICD pipeline.
|- secrets: Definitions of secrets used in the org (secret values are not set in configs).
|- org: org level resources. Resources within this directory should be managed by CICD pipeline.
  |- terragrunt.hcl: root Terragrunt config which defines remote state for all deployments.
  |- project.{PREFIX}-audit: the project to hold all audit logs for the org.
  |- audit: deployment to setup auditing for the org.
  |- iam: org level iam definitions such as org admins.
  |- folder.fda-mystudies: folder to hold all projects related to FDA MyStudies.
    |- project.{PREFIX}-apps: apps project and resources (GKE)
    |- project.{PREFIX}-data: data project and resources (GCS buckets, CloudSQL instances)
    |- project.{PREFIX}-networks: network project and resources (VPC)
    |- project.{PREFIX}-firebase: firebase project (firestores)
```

## Deployment Steps

1.  Authenticate as a super admin using `gcloud auth login [ACCOUNT]`.

    WARNING: remember to run `gcloud auth revoke` to logout as a super admin.
    Being logged in as a super admin beyond the initial setup is dangerous!

1.  Checkout the Terraform configs and set some helper environment variables.

    ```
    $ git clone my-repo
    $ cd my-repo
    $ ROOT=$PWD
    ```

    If you would like to deploy the same infrastructure based on the Terraform
    configs in this directory but in a different organization with different
    resource prefix or namings, use the `rename.sh` script.

1.  The bootstrap config must be deployed first in order to create the `devops`
    project which will host your Terraform state and CICD pipelines.

    ```
    $ cd $ROOT/bootstrap
    $ terraform init
    $ terraform plan
    $ terraform apply
    ```

    Your `devops` project should now be ready.

1.  Backup the state of the `devops` project to the newly created state bucket
    by uncommenting out the `terraform` block in `$ROOT/bootstrap/main.tf` and
    running:

    ```
    $ terraform init
    ```

1.  Deploy secrets used in the org in the `devops` project.

    ```
    $ cd $ROOT/secrets
    $ terraform init
    $ terraform plan
    $ terraform apply
    ```

    After the secrets have been created, you must go to the Google Cloud
    Console, open `Security` --> `Secret Manager` and fill in their values.

1.  Run `copy_client_info_to_sql.sh` script to copy client into from secrets
    into CloudSQL.

1.  Setup Firestore database. This needs to be done on Google Cloud Console web
    UI. Steps:
    1.  Navigate to {PREFIX}-firebase on https://console.cloud.google.com/.
    1.  Select "Firestore" > "Data" from the top-left dropdown.
    1.  Click "SELECT NATIVE MODE" button.
    1.  Select a location from the dropdown. Ideally this should be close to
        where the apps will be running.
    1.  Click "CREATE DATABASE" button.

1.  Follow `$ROOT/cicd/README.md` to set up CICD pipelines for Terraform
    configs.

1.  Follow `$ROOT/kubernetes/README.md` to deploy the Kubernetes resources in
    the GKE cluster.

1.  Revoke your super admin access by running `gcloud auth revoke` and
    authenticate as a normal user for daily activities.
