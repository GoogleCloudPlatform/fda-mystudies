# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "sql_password" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-sql-default-user-password"
}

data "google_secret_manager_secret_version" "registration_client_id" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-registration-client-id"
}

data "google_secret_manager_secret_version" "registration_client_secret" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-registration-client-secret"
}

data "google_secret_manager_secret_version" "wcp_user" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-wcp-user"
}

data "google_secret_manager_secret_version" "wcp_pass" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-wcp-pass"
}

data "google_secret_manager_secret_version" "email_address" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-email-address"
}

data "google_secret_manager_secret_version" "email_password" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-email-password"
}


# Secrets from Secret Manager.
resource "kubernetes_secret" "cloudsql_db_credentials" {
  metadata {
    name = "cloudsql-db-credentials"
  }

  data = {
    username = var.sql_instance_user
    password = data.google_secret_manager_secret_version.sql_password.secret_data
    dbname   = var.sql_instance_name
  }
}

resource "kubernetes_secret" "response_server_credentials" {
  metadata {
    name = "response-server-credentials"
  }

  data = {
    REGISTRATION_CLIENT_ID     = data.google_secret_manager_secret_version.registration_client_id.secret_data
    REGISTRATION_CLIENT_SECRET = data.google_secret_manager_secret_version.registration_client_secret.secret_data
    WCP_USER                   = data.google_secret_manager_secret_version.wcp_user.secret_data
    WCP_PASS                   = data.google_secret_manager_secret_version.wcp_pass.secret_data
  }
}

resource "kubernetes_secret" "email_credentials" {
  metadata {
    name = "email-credentials"
  }

  data = {
    email_address  = data.google_secret_manager_secret_version.email_address.secret_data
    email_password = data.google_secret_manager_secret_version.email_password.secret_data
  }
}

# Secrets from service accounts.
resource "google_service_account_key" "gke_cluster_service_account_key" {
  service_account_id = module.heroes_hat_cluster.service_account
}

resource "kubernetes_secret" "cloudsql_instance_credentials" {
  metadata {
    name = "cloudsql-instance-credentials"
  }
  data = {
    "sql_credentials.json" = base64decode(google_service_account_key.gke_cluster_service_account_key.private_key)
  }
}

resource "kubernetes_secret" "response_server_ws_gcloud_key" {
  metadata {
    name = "response-server-ws-gcloud-key"
  }
  data = {
    "key.json" = base64decode(google_service_account_key.gke_cluster_service_account_key.private_key)
  }
}

resource "kubernetes_secret" "user_registration_server_ws_gcloud_key" {
  metadata {
    name = "user-registration-server-ws-gcloud-key"
  }
  data = {
    "key.json" = base64decode(google_service_account_key.gke_cluster_service_account_key.private_key)
  }
}
