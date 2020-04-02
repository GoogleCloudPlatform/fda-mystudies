# TODO(user): Uncomment after deployment and run `terraform init`.
# terraform {
#   backend "gcs" {
#     bucket = "heroes-hat-dev-terraform-state-08679"
#     prefix = "bootstrap"
#   }
# }


module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 7.0"

  name                    = var.project_id
  org_id                  = var.org_id
  billing_account         = var.billing_account
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
}

module "state_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = var.state_bucket
  project_id = var.project_id
  location   = var.storage_location
}
