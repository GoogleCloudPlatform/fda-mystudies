# This folder contains Terraform resources to for secrets stored in Google Cloud Secret Manager.

terraform {
  backend "gcs" {
    bucket = "heroes-hat-dev-terraform-state-08679"
    prefix = "secrets"
  }
}

resource "google_secret_manager_secret" "secrets" {
  provider = google-beta

  for_each = toset([
    "my-studies-sql-default-user-password",
    "my-studies-registration-client-id",
    "my-studies-registration-client-secret",
    "my-studies-wcp-user",
    "my-studies-wcp-pass",
    "my-studies-email-address",
    "my-studies-email-password",
  ])

  secret_id = each.key
  project   = var.project_id

  replication {
    automatic = true
  }
}
