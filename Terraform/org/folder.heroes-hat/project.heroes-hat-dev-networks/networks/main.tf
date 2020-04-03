terraform {
  backend "gcs" {}
}

resource "google_compute_shared_vpc_host_project" "host" {
  project = var.project_id
}

resource "google_compute_shared_vpc_service_project" "service_projects" {
  for_each        = var.service_projects
  host_project    = google_compute_shared_vpc_host_project.host.project
  service_project = each.value
}

module "private" {
  source  = "terraform-google-modules/network/google"
  version = "~> 2.0"

  project_id   = var.project_id
  network_name = "private"

  subnets = []
}

module "cloudsql_private_service_access" {
  source      = "GoogleCloudPlatform/sql-db/google//modules/private_service_access"
  version = "~> 3.0"

  project_id  = var.project_id
  vpc_network = module.private.network_name
}
