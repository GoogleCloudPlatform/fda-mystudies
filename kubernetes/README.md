# Kubernetes Setup

This directory contains some Kubernetes resources common to all the apps.

## Kubernetes Files Locations

All files below are relative to the root of the repo.

*   kubernetes/
    *   cert.yaml
        *   A Kubernetes ManagedCertificate for using
            [Google-managed SSL certificates](https://cloud.google.com/kubernetes-engine/docs/how-to/managed-certs).
    *   ingress.yaml
        *   A Kubernetes Ingress for routing HTTP calls to services in the
            cluster.
    *   pod_security_policy.yaml
        *   A restrictive Pod Security Policy that applies to the cluster apps.
    *   pod_security_policy-istio.yaml
        *   A looser Pod Security Policy that only applies to Istio containers
            in the cluster.
    *   kubeapply.sh
        *   A helper script that applies all resources to the cluster. Not
            required, the manual steps will be described below.
*   auth-server-ws/
    *   tf-deployment.yaml
        *   A Kubernetes Deployment, deploying the app along with its secrets.
        *   This is forked from deployment.yaml with modifications for the
            Terraform setup.
    *   tf-service.yaml
        *   A Kubernetes Service, exposing the app to communicate with other
            apps and the Ingress.
        *   This is forked from service.yaml with modifications for the
            Terraform setup.
*   response-server-ws/
    *   <same as auth-server-ws>
*   WCP/
    *   <same as auth-server-ws>
*   WCP-WS/
    *   <same as auth-server-ws>
*   user-registration-server-ws/
    *   <same as auth-server-ws>

## Setup

### Prerequisites

Install the following dependencies and add them to your PATH:

*   [gcloud](https://cloud.google.com/sdk/gcloud)
*   [gsutil](https://cloud.google.com/storage/docs/gsutil_install)
*   [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl)

Find the following project IDs:

*   `<apps-project-id>`
*   `<data-project-id>`

Substitute these in the following instructions.

### Terraform

Follow the [Terraform README.md](../Terraform/README.md) to create the
infrastructure. This will create a GKE cluster and a Cloud SQL MySQL database
instance.

### SQL

There are some SQL dump files in this repo that need to be imported before
deploying the apps.

The gcloud import command only imports from GCS buckets. The Terraform setup
creates a bucket and gives the SQL instance permission to read files from it.
The bucket is named "<data-project>-sql-import"; for example,
"heroes-hat-dev-data-sql-import"

Upload the SQL files to the bucket:

```
$ gsutil cp \
  ./auth-server-ws/auth_server_db_script.sql \
  ./WCP/sqlscript/* \
  ./response-server-ws/mystudies_response_server_db_script.sql \
  ./user-registration-server-ws/sqlscript/mystudies_app_info_update_db_script.sql \
  ./user-registration-server-ws/sqlscript/mystudies_user_registration_db_script.sql \
gs://<data-project-id>-sql-import
```

Find the name of your Cloud SQL DB instance. If looking at the GCP Console, this
is just the instance name, is **not** the "Instance connection name". Example:
if the connection name is "myproject-data:us-east1:my-studies", you should use
just "my-studies".

Import the scripts, in this order:

```
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/auth_server_db_script.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/procedures.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/version_info_script.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/HPHC_My_Studies_DB_Create_Script.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/mystudies_response_server_db_script.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/mystudies_app_info_update_db_script.sql
$ gcloud sql import sql --project=<data-project-id> <instance-name> gs://<data-project-id>-sql-import/mystudies_user_registration_db_script.sql
```

### Kubernetes Config Values

You may need to make some changes to the Kubernetes configs to match your
organization and deployment.

In each tf-deployment.yaml file:

*   For all images except `gcr.io/cloudsql-docker/gce-proxy`, replace the
    `gcr.io/<project>` part with `gcr.io/<apps-project-id>`
*   For the cloudsql-proxy container, set the `-instances` flag with
    `-instances=<cloudsq-instance-connection-name>=tcp:3306`

In the ./kubernetes/cert.yaml file:

*   Change the name and domain to match your organization.

In the ./kubernetes/ingress.yaml file:

*   Change the `networking.gke.io/managed-certificates` annotation to match the
    name in ./kubernetes/cert.yaml.
*   Change the name and the `kubernetes.io/ingress.global-static-ip-name`
    annotation to match your organization.

### GKE Cluster - Terraform

Some Kubernetes resources are managed through Terraform, due to ease of
configuration. These need to be applied after the cluster already exists, and
due to the configuration of
[Master Authorized Networks](https://cloud.google.com/kubernetes-engine/docs/how-to/authorized-networks),
they can't be applied by the CI/CD automation.

First, authenticate via gcloud:

```
$ gcloud auth login
$ gcloud auth application-default login
```

Enter the Kubernetes Terraform directory

```
$ cd Terraform/kubernetes/
```

**Edit the file `terraform.tfvars`. Make sure the projects and cluster
information is correct.**

Init, plan, and apply the Terraform configs:

```
$ terraform init
$ terraform plan
$ terraform apply
```

(Optional) Lastly, revoke gcloud authentication

```
$ gcloud auth revoke
$ gcloud auth application-default revoke
```

### GKE Cluster - kubectl

Run all commands below from the repo root.

First, get kubectl credentials so you can interact with the cluster:

```
$ gcloud container clusters get-credentials "<cluster-name>" --region="<region>" --project="<apps-project-id>"
```

Apply the pod security policies:

```
$ kubectl apply \
  -f ./kubernetes/pod_security_policy.yaml \
  -f ./kubernetes/pod_security_policy-istio.yaml
```

Apply all deployments:

```
$ kubectl apply \
  -f ./WCP-WS/tf-deployment.yaml \
  -f ./response-server-ws/tf-deployment.yaml \
  -f ./user-registration-server-ws/tf-deployment.yaml \
  -f ./WCP/tf-deployment.yaml \
  -f ./auth-server-ws/tf-deployment.yaml
```

Apply all services:

```
$ kubectl apply \
  -f ./WCP-WS/tf-service.yaml \
  -f ./response-server-ws/tf-service.yaml \
  -f ./user-registration-server-ws/tf-service.yaml \
  -f ./WCP/tf-service.yaml \
  -f ./auth-server-ws/tf-service.yaml
```

Apply the certificate and the ingress:

```
$ kubectl apply \
  -f ./kubernetes/cert.yaml \
  -f ./kubernetes/ingress.yaml
```

## Troubleshooting

If the cluster has issues, there are a few things you can check:

*   Wait. It can take some time for all deployments to come up.
*   Run `kubectl describe pods` and `kubectl logs <pod> <container>`. A useful
    container to look at is `cloudsql-proxy`, to see if the DB connection was
    established correctly.
*   Make sure all the secrets in Secret Manager have values and are not empty.
*   Make sure Pod Security Polices were applied. The cluster has enforcement
    enabled, and will not start any containers if there are no Pod Security
    Policies.
*   Follow a troubleshooting guide. Examples are
    [this](https://learnk8s.io/troubleshooting-deployments) and
    [this](https://kubernetes.io/docs/tasks/debug-application-cluster/debug-cluster/).
