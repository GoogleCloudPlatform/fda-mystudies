This directory defines resources needed to setup CICD pipelines of Terraform
configs.

## Setup

In order to use Cloud Build and Cloud Build Triggers with GitHub, First follow
[installing_the_cloud_build_app](https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app)
to install the Cloud Build app and connect your GitHub repository to your Cloud
project. This currently cannot be done through automation.

Once the GitHub repo is connected, run the following commands in this directory
to enable necessary APIs, grant Cloud Build Service Account necessary
permissions and create Cloud Build Triggers:

```
$ terraform init
$ terraform plan
$ terraform apply
```

Two presubmit triggers are created by default and results are posted in the Pull
Request. Failing these presubmits will block Pull Request submission.

1.  `tf-validate`: Perform Terraform format and syntax check.
1.  `tf-plan`: Generate speculative plans to show a set of possible changes if
    the pending config changes are deployed.

Optionally, set `continuous_deployment_enabled` to `true` in `terraform.tfvars`
to create an additional Cloud Build Trigger and grant the Cloud Build Service
Account broder permissions to automaticaly apply the config changes to GCP after
the Pull Request is approved and submitted.

After the triggers are created, to temporarily disable or re-enable them, set
the `trigger_disabled` in `terraform.tfvars` to `true` or `false` and apply the
changes by running:

```
$ terraform init
$ terraform plan
$ terraform apply
```

## Operation

### Continuous Integration (presubmit)

Presubmit Cloud Build results will be posted as a Cloud Build job link in the
Pull Request, and they should be configured to block Pull Request submission.

Every new push to the Pull Request at the configured branches will automatically
trigger presubmit runs. To manually re-trigger CI jobs, comment `/gcbrun` in the
Pull Ruquest.

### Continuous Deployment (postsubmit)

Postsubmit Cloud Build job will automatically start when a Pull Ruquest is
submitted to a configured branch. To view the result of the Cloud Build run, go
to https://console.cloud.google.com/cloud-build/builds and look for your commit
to view the Cloud Build job triggered by your merged commit.
