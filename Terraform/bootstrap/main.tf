# This folder contains Terraform resources to setup the devops project, which includes:
# - The devops project itself,
# - API to enable in the devops project,
# - Deletion lien of the devops project,
# - A Cloud Storage bucket to store Terraform states for all deployments,
# - Project level IAM bindings for the devops project,
# - Org level IAM bindings for the org.

# ====================================================================================
# TODO(user): Uncomment after initial deployment and run `terraform init`.
terraform {
  backend "gcs" {
    bucket = "heroes-hat-dev-terraform-state-08679"
    prefix = "bootstrap"
  }
}
# ======================================================================================

# Devops project, with APIs to enable and deletion lien created.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 7.0"

  name                    = var.devops_project_id
  org_id                  = var.org_id
  billing_account         = var.billing_account
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "cloudbuild.googleapis.com",
    "secretmanager.googleapis.com",
  ]
}

# Terraform state bucket, hosted in the devops project.
module "state_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = var.state_bucket
  project_id = module.project.project_id
  location   = var.storage_location
}

# Project level IAM bindings for devops project owners.
resource "google_project_iam_binding" "devops_owners" {
  project = module.project.project_id
  role    = "roles/owner"
  members = var.devops_owners
}

# Org level IAM bindings.
resource "google_organization_iam_member" "org_admin" {
  org_id = var.org_id
  role   = "roles/resourcemanager.organizationAdmin"
  member = var.org_admin
}
