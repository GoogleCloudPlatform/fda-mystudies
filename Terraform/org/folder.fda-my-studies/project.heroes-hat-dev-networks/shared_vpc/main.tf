terraform {
  backend "gcs" {}
}

resource "google_compute_shared_vpc_host_project" "host" {
  project = var.project_id
}

resource "google_compute_shared_vpc_service_project" "service_projects" {
  for_each        = toset([for p in var.service_projects : p.id])
  host_project    = google_compute_shared_vpc_host_project.host.project
  service_project = each.value
}

locals {
  gke_service_projects = [for p in var.service_projects : p if p.gke]
}

resource "google_project_iam_member" "k8s_host_service_agent_users" {
  for_each = { for p in local.gke_service_projects : p.id => p.num }
  project  = var.project_id
  role     = "roles/container.hostServiceAgentUser"
  member   = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}

resource "google_project_iam_member" "k8s_network_users" {
  for_each = { for p in local.gke_service_projects : p.id => p.num }
  project  = var.project_id
  role     = "roles/compute.networkUser"
  member   = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}


resource "google_project_iam_member" "google_apis_network_users" {
  for_each = { for p in local.gke_service_projects : p.id => p.num }
  project  = var.project_id
  role     = "roles/compute.networkUser"
  member   = "serviceAccount:${each.value}@cloudservices.gserviceaccount.com"
}
