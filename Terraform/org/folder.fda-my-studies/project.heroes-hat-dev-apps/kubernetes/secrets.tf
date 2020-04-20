data "google_container_cluster" "gke_cluster" {
  name     = var.my_studies_cluster.name
  location = var.my_studies_cluster.location
  project  = var.project_id
}


provider "kubernetes" {
  host                   = data.google_container_cluster.gke_cluster.endpoint
  client_certificate     = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_certificate)
  client_key             = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_key)
  cluster_ca_certificate = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.cluster_ca_certificate)
}

locals {
  apps = [
    "auth-server",
    "response-server",
    "study-designer",
    "study-meta-data",
    "user-registration",
  ]
}


# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "secrets" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = each.key

  for_each = toset(concat(
    [
      "my-studies-registration-client-id",
      "my-studies-registration-client-secret",
      "my-studies-wcp-user",
      "my-studies-wcp-pass",
      "my-studies-email-address",
      "my-studies-email-password",
    ],
    formatlist("%s-db-user", local.apps),
    formatlist("%s-db-password", local.apps))
  )
}

# Secrets from Secret Manager.
resource "kubernetes_secret" "apps_db_credentials" {
  for_each = toset(local.apps)

  metadata {
    name = "${each.key}-db-credentials"
  }

  data = {
    username = data.google_secret_manager_secret_version.secrets["${each.key}-db-user"].secret_data
    password = data.google_secret_manager_secret_version.secrets["${each.key}-db-password"].secret_data
    dbname   = var.sql_instance_name
  }
}

resource "kubernetes_secret" "response_server_credentials" {
  metadata {
    name = "response-server-credentials"
  }

  data = {
    REGISTRATION_CLIENT_ID     = data.google_secret_manager_secret_version.secrets["my-studies-registration-client-id"].secret_data
    REGISTRATION_CLIENT_SECRET = data.google_secret_manager_secret_version.secrets["my-studies-registration-client-secret"].secret_data
    WCP_USER                   = data.google_secret_manager_secret_version.secrets["my-studies-wcp-user"].secret_data
    WCP_PASS                   = data.google_secret_manager_secret_version.secrets["my-studies-wcp-pass"].secret_data
  }
}

resource "kubernetes_secret" "email_credentials" {
  metadata {
    name = "email-credentials"
  }

  data = {
    email_address  = data.google_secret_manager_secret_version.secrets["my-studies-email-address"].secret_data
    email_password = data.google_secret_manager_secret_version.secrets["my-studies-email-password"].secret_data
  }
}

# gcloud keys from service accounts
resource "google_service_account_key" "apps_service_account_keys" {
  for_each = toset(local.apps)

  # Expected format:
  # {auth-server="<auth_server_service_account@<project>.iam.gserverviceaccount.com", ...}
  service_account_id = var.apps_service_accounts[each.key].unique_id
}

resource "kubernetes_secret" "apps_gcloud_keys" {
  for_each = toset(local.apps)

  metadata {
    name = "${each.key}-gcloud-key"
  }
  data = {
    "key.json" = base64decode(google_service_account_key.apps_service_account_keys[each.key].private_key)
  }
}
