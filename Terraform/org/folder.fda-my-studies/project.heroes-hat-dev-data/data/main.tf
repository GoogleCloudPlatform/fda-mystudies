terraform {
  backend "gcs" {}
}

module "images_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "heroes-hat-dev-images"
  project_id = var.project_id
  location   = var.storage_location
}

module "images_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "heroes-hat-dev-my-study-consent-documents"
  project_id = var.project_id
  location   = var.storage_location
}

module "my_studies_cloudsql" {
  source  = "GoogleCloudPlatform/sql-db/google//modules/mysql"
  version = "~> 3.0"

  name        = "my-studies"
  project_id  = var.project_id
  region      = var.cloudsql_region
  zone        = var.cloudsql_zone
  vpc_network = var.network

  failover_replica                                 = true
  failover_replica_tier                            = "db-n1-standard-1"
  failover_replica_zone                            = "b"
  failover_replica_activation_policy               = "ALWAYS"
  failover_replica_disk_autoresize                 = true
  failover_replica_disk_type                       = "PD_SSD"
  failover_replica_maintenance_window_day          = 3
  failover_replica_maintenance_window_hour         = 20
  failover_replica_maintenance_window_update_track = "canary"

  failover_replica_configuration = {
    failover_target           = true
    dump_file_path            = null
    connect_retry_interval    = null
    ca_certificate            = null
    client_certificate        = null
    client_key                = null
    master_heartbeat_period   = null
    password                  = null
    ssl_cipher                = null
    username                  = null
    verify_server_certificate = null
  }
}
