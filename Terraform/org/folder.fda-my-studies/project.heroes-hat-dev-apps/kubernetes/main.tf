terraform {
  backend "gcs" {}
}

data "google_client_config" "default" {}

provider "kubernetes" {
  load_config_file       = false
  host                   = var.my_studies_cluster.endpoint
  token                  = data.google_client_config.default.access_token
  cluster_ca_certificate = base64decode(var.my_studies_cluster.ca_certificate)
}
