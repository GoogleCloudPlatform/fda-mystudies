# Deploy FDA MyStudies using Terraform

These directories define the entire infrastructure necessary to run FDA
MyStudies on Google Cloud.

This Terraform deployment is an adaptation of Google Cloud's
[HIPAA-aligned architecture](https://cloud.google.com/solutions/architecture-hipaa-aligned-project).
This approach to project configuration and deployment is explained in the
["Setting up a HIPAA-aligned project"](https://cloud.google.com/solutions/setting-up-a-hipaa-aligned-project)
solution guide.

This document provides instructions for deploying FDA MyStudies on Google Cloud
in using infrastrucutre-as-code in approximately 1 hour. A video tutorial that
walks the user through these steps is available upon request.

## Prerequisites

1. Install the following dependencies and add them to your PATH:

    - [GCloud](https://cloud.google.com/sdk/gcloud)
    - [Terraform](https://www.terraform.io/)
    - [Terragrunt](https://terragrunt.gruntwork.io/)

1. Get familiar with [GCP](https://cloud.google.com/docs/overview),
    [Terraform](https://www.terraform.io/intro/index.html) and
    [Terragrunt](https://blog.gruntwork.io/terragrunt-how-to-keep-your-terraform-code-dry-and-maintainable-f61ae06959d8).

    The infrastructure is deployed using Terraform, which is an industry
    standard for defining infrastructure-as-code. Terragrunt is used as a
    wrapper around Terraform to manage multiple Terraform deployments and reduce
    duplication.

1. Setup your
    [organization](https://cloud.google.com/resource-manager/docs/creating-managing-organization)
    for GCP resources and [G Suite Domain](https://gsuite.google.com/) for
    groups.

1. [Create administrative groups](https://support.google.com/a/answer/33343?hl=en)
    in the G Suite Domain:

    - {PREFIX}-org-admins@{DOMAIN}.com: This group has administrative access
        to the entire org. This group can be used in break-glass situations to
        give humans access to the org to make changes.

    - {PREFIX}-devops-owners@{DOMAIN}.com: This group has owners access to the
        devops project to make changes to the CICD project or make changes to
        the Terraform state.

    - {PREFIX}-auditors@{DOMAIN}.com: This group has security reviewer
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

- **main.tf**: This file defines the Terraform resources and modules to
    manage. For more complex deployments, there may be multiple .tf files that
    define resources.

- **variables.tf**: This file defines any input variables that the deployment
    can take.

- **outputs.tf**: This file defines any outputs from this deployment. These
    values can be used by other deployments.

- **terraform.tfvars**: This file defines values for the input variables.

- **terragrunt.hcl**: This file defines dependencies between other
    deployments, the remote state, and input values from other dependent
    deployments.

To see what resources each deployment provisions, check out the comments in each
**main.tf** file.

## Layout

```bash
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

### Phase1: Devops Project, Secrets and CICD

1. Authenticate as a super admin using `gcloud auth login [ACCOUNT]`.

    WARNING: remember to run `gcloud auth revoke` to logout as a super admin.
    Being logged in as a super admin beyond the initial setup is dangerous!

1. Checkout the Terraform configs and set some helper environment variables.

    ```bash
    git clone my-repo
    cd my-repo
    ROOT=$PWD
    ```

    If you would like to deploy the same infrastructure based on the Terraform
    configs in this directory but in a different organization with different
    resource prefix or namings, use the [rename.sh](./rename.sh) script with
    your environment specific values filled in. Note that
    [rename.sh](./rename.sh) should be run several times with corresponding
    deployment phase block uncommented. Cross reference this `README.md` file
    and documentations in the [rename.sh](./rename.sh) together do complete the
    deployment.

    The [rename.sh](./rename.sh) script uses configs in this directory and copy
    them over with value substitutions to a target local directory to host your
    new final Terraform configs.

#### Devops Project

1. Run [rename.sh](./rename.sh) in this directory (later referenced as the
    original directory) to copy the Deployment Phase 1 configs to your target
    directory.

1. Go to the target directory.

1. Deploy the [bootstrap/](./bootstrap/) folder first to create the `devops`
    project and Terraform state bucket.

    Make sure in your first deployment in a new organization, comment out
    [GCS backend](./bootstrap/main.tf#L32-L35) first.

    ```bash
    cd $ROOT/bootstrap
    terraform init
    terraform apply
    ```

    Your `devops` project should now be ready.

1. Backup the state of the `devops` project to the newly created state bucket
    by uncommenting [GCS backend](./bootstrap/main.tf#L32-L35), running the
    following commands and answer `yes` when prompted.

    ```bash
    terraform init
    ```

#### Secrets

1. Deploy secrets used in the org in the `devops` project.

    ```bash
    cd $ROOT/secrets
    terraform init
    terraform apply
    ```

    After the secrets have been created, you must go to the Google Cloud
    Console, open `Security` --> `Secret Manager` and fill in the values
    for the following secrets:
    - my-studies-sql-default-user-password
    - my-studies-wcp-user
    - my-studies-wcp-pass
    - my-studies-email-address
    - my-studies-email-password
    - mobile-app-appid
    - mobile-app-orgid

#### CICD

1. Follow [CICD README.md](./cicd/README.md) to set up CICD pipelines for
    Terraform configs.

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the rest of Phase 1 resources for
    you.

### Phase2: Networks and Firebase Projects

1. Go to the original directory.

1. Uncomment [Deployment Phase 2](./rename.sh#L101-L103)

1. Comment out [Firestore location and Index](./org/folder.mystudies-demo/project.mystudies-demo-resp-firebase/firebase/main.tf#L27-L61).

1. run [rename.sh](./rename.sh).

1. Go to the target diretory.

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the Phase 2 resources for you.

1. Setup Firestore database. This needs to be done on Google Cloud Console web
    UI. Steps:

    1. Navigate to {PREFIX}-firebase on <https://console.cloud.google.com/.>
    1. Select "Firestore" > "Data" from the top-left dropdown.
    1. Click "SELECT NATIVE MODE" button.
    1. Select a location from the dropdown. Ideally this should be close to
        where the apps will be running.
    1. Click "CREATE DATABASE" button.

1. Uncomment [Firestore location and Index](./org/folder.mystudies-demo/project.mystudies-demo-resp-firebase/firebase/main.tf#L27-L61) and run [rename.sh](./rename.sh).

1. Commit your current local git working dir and send a Pull Request to merge
    these configs.

### Phase3: Apps Project

1. Go to the original directory.

1. Uncomment [Deployment Phase 3](./rename.sh#L106) and run [rename.sh](./rename.sh).

1. Go to the target diretory.

1. Comment out the entire file
    [cloudbuild.tf](./org/folder.mystudies-demo/project.mystudies-demo-apps/apps/cloudbuild.tf).
    This file contains Cloud Build Triggers to auto generate Docker containers
    when new commits are merged to certain branches. Uncomment later if you
    would like to use this feature and follow documentation in that file to
    deploy those triggers.

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the Phase 3 resources for you.

### Phase4: Data Project

1. Go to the original directory.

1. Uncomment [Deployment Phase 4](./rename.sh#L109-L110) and run [rename.sh](./rename.sh).

1. Go to the target diretory.

1. Comment out
    [iam_members](./org/folder.mystudies-demo/project.mystudies-demo-data/data/main.tf#L74-L79).

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the Phase 4 resources for you.

1. Uncomment
    [iam_members](./org/folder.mystudies-demo/project.mystudies-demo-data/data/main.tf#L74-L79).

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy this additional IAM permission for
    you.

1. Follow [Kubernetes README.md](../kubernetes/README.md) to deploy the
    Kubernetes resources in the GKE cluster. Note that the `rename.sh` script
    didn't copy or handle the Kubernetes deployment artifacts.

1. Run [copy_client_info_to_sql.sh](./copy_client_info_to_sql.sh) script to
    copy client info from secrets into CloudSQL.

1. Run [copy_mobile_app_info_to_sql.sh](./copy_mobile_app_info_to_sql.sh)
    script to copy mobile app info from secrets into CloudSQL.

1. Revoke your super admin access by running `gcloud auth revoke` and
    authenticate as a normal user for daily activities.

### Phase 5: Mobile app setups

1. Build and destribute iOS and Android apps following their individual instructions.

1. Once you have setup push notification for the apps, copy the values to their corresponding secrets:
```
# bundleID used for the Android App.
android-bundle-id
# found under settings > cloud messaging in the android app defined in your firebase project.
android-server-key
# bundleID used to build and distribute the iOS App.
ios-bundle-id
# certificate and password generated for APNs.
ios-certificate
ios-certificate-password
```

1. Run [copy_push_notification_info_to_sql.sh](./copy_push_notification_info_to_sql.sh)
    script to copy push notification info from secrets into CloudSQL.