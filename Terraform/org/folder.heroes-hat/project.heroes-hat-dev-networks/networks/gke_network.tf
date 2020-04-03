provider "google" {
  version = "~> 3.12.0"
}

module "gke_network" {
  source       = "terraform-google-modules/network/google"
  version      = "~> 2.0"
  project_id   = var.project_id
  network_name = var.gke_network_name

  subnets = [
    {
      subnet_name   = "auth-server-ws-cluster-subnet"
      subnet_ip     = "10.0.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "auth-server-ws-cluster-master-subnet"
      subnet_ip     = "10.60.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "response-server-ws-cluster-subnet"
      subnet_ip     = "10.1.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "response-server-ws-cluster-master-subnet"
      subnet_ip     = "10.61.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "study-designer-cluster-subnet"
      subnet_ip     = "10.2.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "study-designer-cluster-master-subnet"
      subnet_ip     = "10.62.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "study-meta-data-cluster-subnet"
      subnet_ip     = "10.3.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "study-meta-data-cluster-master-subnet"
      subnet_ip     = "10.63.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "user-registration-server-ws-cluster-subnet"
      subnet_ip     = "10.4.0.0/17"
      subnet_region = var.gke_region
    },
    {
      subnet_name   = "user-registration-server-ws-cluster-master-subnet"
      subnet_ip     = "10.64.0.0/17"
      subnet_region = var.gke_region
    },
  ]

  secondary_ranges = {
    "auth-server-ws-cluster-subnet" = [
      {
        range_name    = "auth-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.0.0/21"
      },
      {
        range_name    = "auth-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.64.0/21"
      },
    ],
    "response-server-ws-cluster-subnet" = [
      {
        range_name    = "response-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.8.0/21"
      },
      {
        range_name    = "response-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.72.0/21"
      },
    ],
    "study-designer-cluster-subnet" = [
      {
        range_name    = "study-designer-cluster-ip-range-pods"
        ip_cidr_range = "192.168.16.0/21"
      },
      {
        range_name    = "study-designer-cluster-ip-range-svc"
        ip_cidr_range = "192.168.80.0/21"
      },
    ],
    "study-meta-data-cluster-subnet" = [
      {
        range_name    = "study-meta-data-cluster-ip-range-pods"
        ip_cidr_range = "192.168.24.0/21"
      },
      {
        range_name    = "study-meta-data-cluster-ip-range-svc"
        ip_cidr_range = "192.168.88.0/21"
      },
    ],
    "user-registration-server-ws-cluster-subnet" = [
      {
        range_name    = "user-registration-server-ws-cluster-ip-range-pods"
        ip_cidr_range = "192.168.32.0/21"
      },
      {
        range_name    = "user-registration-server-ws-cluster-ip-range-svc"
        ip_cidr_range = "192.168.96.0/21"
      },
    ],
  }
}
