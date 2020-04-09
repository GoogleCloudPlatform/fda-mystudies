terraform {
  backend "gcs" {}
}

# Network values are defined the same way in the network component.

# From
# https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/modules/safer-cluster-update-variant
module "heroes_hat_cluster" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name                   = "heroes-hat-cluster"
  kubernetes_version     = "1.14.10-gke.27"
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "heroes-hat-cluster-ip-range-pods"
  ip_range_services      = "heroes-hat-cluster-ip-range-svc"
  master_ipv4_cidr_block = "192.168.0.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}
