module "private" {
  source  = "terraform-google-modules/network/google"
  version = "~> 2.0"

  project_id   = var.project_id
  network_name = "private"

  subnets = []
}

module "cloudsql_private_service_access" {
  source      = "terraform-google-modules/sql-db/google//modules/private_service_access"
  version = "~> 3.0"

  project_id  = var.project_id
  vpc_network = module.private.network_name
}
