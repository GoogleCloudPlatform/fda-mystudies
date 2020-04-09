terraform {
  backend "gcs" {}
}

locals {
  gke_clusters_subnet_name = "gke-clusters-subnet"
}

module "private" {
  source  = "terraform-google-modules/network/google"
  version = "~> 2.0"

  project_id   = var.project_id
  network_name = "private"

  # Multiple clusters can be in the same network and subnet.
  subnets = [
    {
      subnet_name      = local.gke_clusters_subnet_name
      subnet_ip        = "10.0.0.0/17"
      subnet_region    = var.gke_region
      subnet_flow_logs = "true"

      # Needed for access to Cloud SQL.
      subnet_private_access = "true"
    },
  ]

  # These ranges must not overlap.
  # See https://cloud.google.com/kubernetes-engine/docs/how-to/alias-ips#cluster_sizing_secondary_range_pods for how many nodes the /20 ranges get.
  secondary_ranges = {
    "${local.gke_clusters_subnet_name}" = [
      # The Heroes Hat GKE cluster.
      # /14 is the default size for the subnet's secondary IP range for Pods when the secondary range assignment method is managed by GKE, so imitate that.
      # Calculated using http://www.davidc.net/sites/default/subnets/subnets.html
      {
        range_name    = "heroes-hat-cluster-ip-range-pods"
        ip_cidr_range = "172.16.0.0/14"
      },
      {
        range_name    = "heroes-hat-cluster-ip-range-svc"
        ip_cidr_range = "172.20.0.0/14"
      },
      # Remove after TF runs.
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

  # Be explicit to avoid overlapping with the GKE ranges.
  # The GKE subnet range is 10.0.0.0/17, which goes up to 10.0.127.254; here
  # we're using 10.1.0.0/17, which does not overlap at all with the GKE range.
  # The default prefix length is /16, so /17 is close enough and probably safe.
  ip_version    = "IPV4"
  address       = "10.1.0.0"
  prefix_length = "17"
}
