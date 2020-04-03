terraform {
  backend "gcs" {}
}

module "example_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-bucket"
  project_id = var.project_id
  location   = var.storage_location
}

module "example_cloudsql" {
  source  = "terraform-google-modules/sql-db/google//modules/mysql"
  version = "~> 3.0"

  name        = "example-cloudsql"
  project_id  = var.project_id
  region      = var.cloudsql_region
  zone        = var.cloudsql_zone
  vpc_network = var.network
}
