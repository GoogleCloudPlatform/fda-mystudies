resource "google_compute_shared_vpc_host_project" "host" {
  project = var.project_id
}

resource "google_compute_shared_vpc_service_project" "service_projects" {
  for_each        = toset([for p in var.service_projects : p.id])
  host_project    = google_compute_shared_vpc_host_project.host.project
  service_project = each.value
}

locals {
  gke_service_projects          = [for p in var.service_projects : p if p.has_gke]
  gke_service_project_id_to_num = { for p in local.gke_service_projects : p.id => p.num }
  gke_subnet                    = module.private.subnets["${var.gke_region}/${local.gke_clusters_subnet_name}"]
}

resource "google_project_iam_member" "k8s_host_service_agent_users" {
  for_each = local.gke_service_project_id_to_num
  project  = var.project_id
  role     = "roles/container.hostServiceAgentUser"
  member   = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}

resource "google_compute_subnetwork_iam_member" "k8s_network_users" {
  for_each   = local.gke_service_project_id_to_num
  subnetwork = local.gke_subnet.id
  region     = local.gke_subnet.region
  role       = "roles/compute.networkUser"
  member     = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}


resource "google_compute_subnetwork_iam_member" "google_apis_network_users" {
  for_each   = local.gke_service_project_id_to_num
  subnetwork = local.gke_subnet.id
  region     = local.gke_subnet.region
  role       = "roles/compute.networkUser"
  member     = "serviceAccount:${each.value}@cloudservices.gserviceaccount.com"
}
