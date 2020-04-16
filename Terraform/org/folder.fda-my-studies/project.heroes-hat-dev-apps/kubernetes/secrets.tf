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


# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "secrets" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = each.key

  for_each = toset([
    "my-studies-registration-client-id",
    "my-studies-registration-client-secret",
    "my-studies-wcp-user",
    "my-studies-wcp-pass",
    "my-studies-email-address",
    "my-studies-email-password",
  ])
}

# Secrets from Secret Manager.
resource "kubernetes_secret" "cloudsql_db_credentials" {
  metadata {
    name = "cloudsql-db-credentials"
  }

  data = {
    username = var.sql_instance_user
    password = var.sql_instance_user_password
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

# Secrets from service accounts.
resource "google_service_account_key" "gke_cluster_service_account_key" {
  service_account_id = var.my_studies_cluster.service_account
}

resource "kubernetes_secret" "cloudsql_instance_credentials" {
  metadata {
    name = "cloudsql-instance-credentials"
  }
  data = {
    "sql_credentials.json" = base64decode(google_service_account_key.gke_cluster_service_account_key.private_key)
  }
}

