terraform {
  backend "gcs" {}
}

locals {
  gke_clusters_subnet_name = "gke-clusters-subnet"
}

resource "google_compute_shared_vpc_host_project" "host" {
  project = var.project_id
}

resource "google_compute_shared_vpc_service_project" "service_projects" {
  for_each        = toset(var.service_projects)
  host_project    = google_compute_shared_vpc_host_project.host.project
  service_project = each.value
}

module "private" {
  source  = "terraform-google-modules/network/google"
  version = "~> 2.0"

  project_id   = var.project_id
  network_name = "private"

  # All the clusters can be in the same network and subnet.
  subnets = [
    {
      subnet_name   = local.gke_clusters_subnet_name
      subnet_ip     = "10.0.0.0/17"
      subnet_region = var.gke_region
    },
  ]

  # These ranges must not overlap.
  # See https://cloud.google.com/kubernetes-engine/docs/how-to/alias-ips#cluster_sizing_secondary_range_pods for how many nodes the /20 ranges get.
  secondary_ranges = {
    "${local.gke_clusters_subnet_name}" = [
      # Auth server.
      {
        range_name    = "auth-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.0.0/20"
      },
      {
        range_name    = "auth-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.16.0/20"
      },

      # Response server.
      {
        range_name    = "response-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.32.0/20"
      },
      {
        range_name    = "response-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.48.0/20"
      },

      # Study designer.
      {
        range_name    = "study-designer-cluster-ip-range-pods"
        ip_cidr_range = "192.168.64.0/20"
      },
      {
        range_name    = "study-designer-cluster-ip-range-svc"
        ip_cidr_range = "192.168.80.0/20"
      },

      # Study metadata.
      {
        range_name    = "study-meta-data-cluster-ip-range-pods"
        ip_cidr_range = "192.168.96.0/20"
      },
      {
        range_name    = "study-meta-data-cluster-ip-range-svc"
        ip_cidr_range = "192.168.112.0/20"
      },

      # User registration server.
      {
        range_name    = "user-registration-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.128.0/20"
      },
      {
        range_name    = "user-registration-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.144.0/20"
      },
    ],
  }
}

module "cloudsql_private_service_access" {
  source  = "GoogleCloudPlatform/sql-db/google//modules/private_service_access"
  version = "~> 3.0"

  project_id  = var.project_id
  vpc_network = module.private.network_name
}
