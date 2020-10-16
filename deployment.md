# Deploy FDA MyStudies using Terraform Engine and Terraform

This document provides instructions for deploying FDA MyStudies on Google Cloud
in using infrastrucutre-as-code in approximately 1 hour. A video tutorial that
walks the user through these steps is available upon request.

The provided [template](./deployment.hcl) can be instantiated and used with
[Terraform Engine](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine)
to generate Terraform configs that define and deploy the entire infrastructure.

The generated Terraform configs from the template deploys the FDA MyStudies
infrastructure in a dedicated folder with remote Terraform state management and
CICD pipelines enabled by default. The generated Terraform configs should be
checked-in to a GitHub repository.

This Terraform deployment is an adaptation of Google Cloud's
[HIPAA-aligned architecture](https://cloud.google.com/solutions/architecture-hipaa-aligned-project).
This approach to project configuration and deployment is explained in the
["Setting up a HIPAA-aligned project"](https://cloud.google.com/solutions/setting-up-a-hipaa-aligned-project)
solution guide.

## Prerequisites

Follow
[Prerequisites](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine#prerequisites)
and prepare to deploy FDA MyStudies platform infrastructure in a folder.

In addition, [Create](https://support.google.com/a/answer/33343?hl=en) the
following additional administrative
[IAM](https://cloud.google.com/iam/docs/overview#concepts_related_identity)
groups:

- {PREFIX}-bastion-accessors@{DOMAIN}: This group has permission to access the
    bastion host which can then access the private Cloud SQL instance.

Note: Consider including {ENV} in {PREFIX}.

## Installation

Follow the
[installation instructions](https://github.com/GoogleCloudPlatform/healthcare-data-protection-suite/tree/master/docs/tfengine/#installation)
to install the tfengine binary v0.4.0.

## Layout of the generated Terraform configs

```bash
|- devops/: one time manual deployment to create projects to host Terraform state and CICD pipeline.
|- cicd/: one time manual deployment to create CICD pipelines and configure permissions.
|- audit/: audit project and resources (Log bucket, dataset and sinks).
|- {PREFIX}-{ENV}-apps/: apps project and resources (GKE).
|- {PREFIX}-{ENV}-data/: data project and resources (GCS buckets, CloudSQL instances).
|- {PREFIX}-{ENV}-firebase/: firebase project and resources (firestores).
|- {PREFIX}-{ENV}-networks/: networks project and resources (VPC, bastion host).
|- {PREFIX}-{ENV}-secrets/: secrets project and resources (secrets).
|- kubernetes/: manual kubernetes deployment after the GKE cluster has been created.
```

Each directory (devops/, cicd/, {PREFIX}-{ENV}-apps/, etc) represents one
Terraform deployment. Each deployment will manage specific resources in you
infrastructure.

A deployment typically contains the following files:

- **main.tf**: This file defines the Terraform resources and modules to
    manage.

- **variables.tf**: This file defines any input variables that the deployment
    can take.

- **outputs.tf**: This file defines any outputs from this deployment. These
    values can be used by other deployments.

- **terraform.tfvars**: This file defines values for the input variables.

To see what resources each deployment provisions, check out the comments in both
[deployment.hcl](./deployment.hcl) file and individual **main.tf** file.

## CICD

Deployments listed under `managed_modules` in the `cicd` recipe are configured
to be deployed via CICD pipelines.

The CICD service account can manage a subset of resources (e.g. APIs) within its
own project (`devops` project). This allows users to have low risk changes made
in the `devops` project deployed through the standard Cloud Build pipelines,
without needing to apply it manually. Other changes in the `devops` project
outside the approved set (APIs) will still need to be made manually.

A common use case for this is when adding a new resource in a project that
requires a new API to be enabled. You must add the API in both the resource's
project as well as the `devops` project. With the feature above, the CICD can
deploy both changes for you.

## Deployment steps

Note that the deployemnt steps involve editing the Terraform Engine config and
regenerating the Terraform configs several times.

### Preparation

1. Authenticate as a super admin using `gcloud auth login [ACCOUNT]`.

    WARNING: remember to run `gcloud auth revoke` to logout as a super admin.
    Being logged in as a super admin beyond the initial setup is dangerous!

1. Make a copy of [deployment.hcl](./deployment.hcl) and fill in instance
    specific values, which includes:

    - prefix
    - env
    - domain
    - billing_account
    - parent_id
    - github.owner
    - github.name
    - ...

    You can also change other field such as location of resources, etc to fit
    your use case.

1. Clone the remote GitHub repository locally which will be used to check in
    your Terraform configs and save the local path to an environment variable
    `GIT_ROOT`.

    ```bash
    export GIT_ROOT=/path/to/your/local/repo/fda-mystudies
    ```

1. Save the path to your copy of the template to an environment variable
    `ENGINE_CONFIG`.

    ```bash
    export ENGINE_CONFIG=/path/to/your/local/deployment.hcl
    ```

### Step 1: Deploy Devops project and CICD manually

1. Execute the `tfengine` command to generate the configs. By default, CICD
    will look for Terraform configs under the `terraform/` directory in the
    GitHub repo, so set the `--output_path` to point to the `terraform/`
    directory inside the local root of your GitHub repository.

    Make sure in your first deployment in a new folder, `enable_gcs_backend` in
    $ENGINE_CONFIG is set to `false` or commented out.

    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/terraform
    ```

#### Devops Project

1. Deploy the `devops/` folder first to create the `devops` project and
    Terraform state bucket.

    ```bash
    cd $GIT_ROOT/terraform/devops
    terraform init
    terraform apply
    ```

    Your `devops` project should now be ready.

1. In $ENGINE_CONFIG, set `enable_gcs_backend` to `true`, and regenerate the
    Teraform configs.

    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/terraform
    ```

1. Backup the state of the `devops` project to the newly created state bucket
    by running the following command.

    ```bash
    terraform init -force-copy
    ```

#### CICD pipelines

1. Install the Cloud Build app and
    [connect your GitHub repository](https://console.cloud.google.com/cloud-build/triggers/connect)
    to {PREFIX}-{ENV}-devops project by following the steps in
    [Installing the Cloud Build app](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app).

    To perform this operation, you need Admin permission in that GitHub
    repository.

1. Deploy the `cicd/` folder to set up CICD pipelines.

    ```bash
    cd $GIT_ROOT/terraform/cicd
    terraform init
    terraform apply
    ```

### Step 2: Deploy projects and first set of resources through CICD

1. Add the following items to your `.gitignore` file to avoid accidentally
    commit any `.terraform/` directories, `*.tfstate` or `*.tfstate.backup`
    files generated from previous manual deployments:

    ```bash
    **/.terraform
    *.tfstate
    *.tfstate.*
    ```

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the following resources for you.

    - Audit
        - Project
        - All resources (log sink bucket and dataset)
    - Secrets
        - Project
        - All resources (note some secrets values need to be filled manually)
    - Networks
        - Project
        - All resources (VPC, subnets, bastion host, etc)
    - Apps
        - Project
        - All resources (Service Accounts, GKE, DNS, Binary Authorization,
            etc)
    - Firebase
        - Project
        - Partial resources (Firestore data export buckets, PubSub, etc)
    - Data
        - Project
        - Partial resources (Storage buckets, IAM bindings, etc)

### Step 3: Setup secrets

1. Manually fill in certain secret values. This needs to be done on Google
    Cloud Console web UI. Steps:

    1. Navigate to {PREFIX}-{ENV}-secrets on
        <https://console.cloud.google.com/>.
    1. Select "Security" --> "Secret Manager" from the top-left dropdown.
    1. Fill in the values for the secrets with prefix `manual-`.

### Step 4: Setup Firestore database

1. Setup Firestore database. This needs to be done on Google Cloud Console web
    UI. Steps:

    1. Navigate to {PREFIX}-{ENV}-firebase on
        <https://console.cloud.google.com/>.
    1. Select "Firestore" > "Data" from the top-left dropdown.
    1. Click "SELECT NATIVE MODE" button.
    1. Select a location from the dropdown. Ideally this should be close to
        where the apps will be running.
    1. Click "CREATE DATABASE" button.

### Step 5: Deploy additional Firebase resources and Data resources through CICD

1. In $ENGINE_CONFIG, uncomment the blocks that are marked as *Step 5.1*, *Step
    5.2*, *Step 5.3*, *Step 5.4*, *Step 5.5* and *Step 5.6*. Then regenerate the
    Terraform configs:

    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/terraform
    ```

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the resources for you.

### Step 6: Deploy SQL import bucket IAM members through CICD

1. In $ENGINE_CONFIG, uncomment the blocks that are marked as *Step 6* and
    regenerate the Terraform configs:

    ```bash
    tfengine --config_path=$ENGINE_CONFIG --output_path=$GIT_ROOT/terraform
    ```

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the resources for you.

### Step 7: Deploy Cloud Build Triggers for server containers

1. Install the Cloud Build app and connect your GitHub repository to
    {PREFIX}-{ENV}-apps project by following the steps in
    [Installing the Cloud Build app](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app).

    To perform this operation, you need Admin permission in that GitHub
    repository.

1. In $ENGINE_CONFIG , uncomment the Cloud Build Triggers part in the Apps
    project, and regenerate the Terraform configs.

1. Commit your current local git working dir and send a Pull Request to merge
    these configs. Make sure the presubmit tests pass and get code review
    approvals. The CD job will then deploy the resources for you.

### Step 8: Kubernetes deployment

1. Follow [Kubernetes README.md](../kubernetes/README.md) to deploy the
    Kubernetes resources in the GKE cluster.

### Step 9: Secrets setup

1. Modify [copy_client_info_to_sql.sh](./scripts/copy_client_info_to_sql.sh) to
    reflect proper {PREFIX} and {ENV}, and run to copy client info from secrets
    into CloudSQL.

1. Modify
    [copy_mobile_app_info_to_sql.sh](./scripts/copy_mobile_app_info_to_sql.sh)
    to reflect proper {PREFIX} and {ENV}, and run to copy mobile app info from
    secrets into CloudSQL.

### Step 10: Mobile app setups

1. Build and distribute iOS and Android apps following their individual
    instructions.

1. Once you have set up push notification for the apps, copy the values to their
    corresponding secrets:

    ```bash
    # bundleID used for the Android App.
    manual-android-bundle-id
    # found under settings > cloud messaging in the android app defined in your firebase project.
    manual-android-server-key
    # bundleID used to build and distribute the iOS App.
    manual-ios-bundle-id
    # certificate and password generated for APNs.
    manual-ios-certificate
    manual-ios-certificate-password
    ```

1. Modify
    [copy_push_notification_info_to_sql.sh](./scripts/copy_push_notification_info_to_sql.sh)
    to reflect proper {PREFIX} and {ENV}, and run to copy push notification info
    from secrets into CloudSQL.

### Step 11: Clean up

1. Revoke your super admin access by running `gcloud auth revoke` and
    authenticate as a normal user for daily activities.
