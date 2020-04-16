# This folder contains Terraform resources to setup the audit project, which includes:
# - The audit project itself,
# - API to enable in the audit project,
# - Deletion lien of the audit project,
# - Project level IAM bindings for the audit project.

terraform {
  backend "gcs" {}
}

# Devops project, with APIs to enable and deletion lien created.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 7.0"

  name            = var.name
  org_id          = var.org_id
  folder_id       = var.folder_id
  billing_account = var.billing_account
  lien            = true
  activate_apis = [
    "bigquery.googleapis.com",
    "logging.googleapis.com",
  ]
  default_service_account = "keep"
  skip_gcloud_download    = true
}

# Project level IAM bindings for audit project owners.
resource "google_project_iam_binding" "owners" {
  project = module.project.project_id
  role    = "roles/owner"
  members = var.owners
}
