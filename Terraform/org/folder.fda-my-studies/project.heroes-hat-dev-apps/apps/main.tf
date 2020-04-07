terraform {
  backend "gcs" {}
}

# Network values are defined the same way in the network component.

# From
# https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/modules/safer-cluster-update-variant

# Auth server
module "gke-auth-server" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "auth-server-ws-cluster"
  kubernetes_version     = "1.14.10-gke.24"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "auth-server-ws-cluster-ip-range-pods"
  ip_range_services      = "auth-server-ws-cluster-ip-range-svc"
  master_ipv4_cidr_block = "172.16.0.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}

# Response server
module "gke-response-server" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "response-server-ws-cluster"
  kubernetes_version     = "1.14.10-gke.24"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "response-server-ws-cluster-ip-range-pods"
  ip_range_services      = "response-server-ws-cluster-ip-range-svc"
  master_ipv4_cidr_block = "172.16.1.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}

# Study designer
module "gke-study-designer" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "study-designer-cluster"
  kubernetes_version     = "1.14.10-gke.24"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "study-designer-cluster-ip-range-pods"
  ip_range_services      = "study-designer-cluster-ip-range-svc"
  master_ipv4_cidr_block = "172.16.2.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}

# Study metadata
module "gke-study-metadata" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "study-meta-data-cluster"
  kubernetes_version     = "1.14.10-gke.24"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "study-meta-data-cluster-ip-range-pods"
  ip_range_services      = "study-meta-data-cluster-ip-range-svc"
  master_ipv4_cidr_block = "172.16.3.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}

# User registration server
module "gke-registration-server" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "user-registration-server-ws-cluster"
  kubernetes_version     = "1.14.10-gke.24"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "user-registration-server-ws-cluster-ip-range-pods"
  ip_range_services      = "user-registration-server-ws-cluster-ip-range-svc"
  master_ipv4_cidr_block = "172.16.4.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}
