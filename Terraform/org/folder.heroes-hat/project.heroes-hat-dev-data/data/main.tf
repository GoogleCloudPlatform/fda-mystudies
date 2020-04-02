module "example_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-bucket"
  project_id = var.project_id
  location   = var.storage_location
}
