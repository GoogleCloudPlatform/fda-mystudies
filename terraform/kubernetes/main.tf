# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

terraform {
  required_version = "~> 0.12.0"
  required_providers {
    google      = "~> 3.0"
    google-beta = "~> 3.0"
  }
  backend "gcs" {
    bucket = "example-dev-terraform-state"
    prefix = "kubernetes"
  }
}

data "google_client_config" "default" {}

data "google_container_cluster" "gke_cluster" {
  name     = "example-dev-gke-cluster"
  location = "us-central1"
  project  = "example-dev-apps"
}

provider "kubernetes" {
  load_config_file       = false
  token                  = data.google_client_config.default.access_token
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
    "study-metadata",
    "user-registration",
  ]
  apps_db_names = {
    "auth-server"       = "auth_server"
    "response-server"   = "mystudies_response_server"
    "study-designer"    = "fda_hphc"
    "study-metadata"    = "fda_hphc"
    "user-registration" = "mystudies_userregistration"
  }
  # App codes for auth server authentication.
  auth_server_app_codes = [
    "ma",  # Mobile App
    "urs", # User Registration Server
    "rs",  # Response Server
    "wcp", # Web Config Portal
  ]
}

# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "secrets" {
  provider = google-beta
  project  = "example-dev-secrets"
  secret   = each.key

  for_each = toset(concat(
    [
      "manual-mystudies-wcp-user",
      "manual-mystudies-wcp-password",
      "manual-mystudies-email-address",
      "manual-mystudies-email-password",
    ],
    formatlist("auto-%s-db-user", local.apps),
    formatlist("auto-%s-db-password", local.apps),
    formatlist("auto-mystudies-%s-client-id", local.auth_server_app_codes),
    formatlist("auto-mystudies-%s-secret-key", local.auth_server_app_codes))
  )
}

# Secrets from Secret Manager.
resource "kubernetes_secret" "apps_db_credentials" {
  for_each = toset(local.apps)

  metadata {
    name = "${each.key}-db-credentials"
  }

  data = {
    username = data.google_secret_manager_secret_version.secrets["auto-${each.key}-db-user"].secret_data
    password = data.google_secret_manager_secret_version.secrets["auto-${each.key}-db-password"].secret_data
    dbname   = local.apps_db_names[each.key]
  }
}

# App-specific secrets.
resource "kubernetes_secret" "response_server_secrets" {
  metadata {
    name = "response-server-secrets"
  }

  data = {
    REGISTRATION_CLIENT_ID     = data.google_secret_manager_secret_version.secrets["auto-mystudies-urs-client-id"].secret_data
    REGISTRATION_CLIENT_SECRET = data.google_secret_manager_secret_version.secrets["auto-mystudies-urs-secret-key"].secret_data
    WCP_USER                   = data.google_secret_manager_secret_version.secrets["manual-mystudies-wcp-user"].secret_data
    WCP_PASS                   = data.google_secret_manager_secret_version.secrets["manual-mystudies-wcp-password"].secret_data
  }
}

resource "kubernetes_secret" "user_registration_secrets" {
  metadata {
    name = "user-registration-secrets"
  }

  data = {
    CLIENT_ID       = data.google_secret_manager_secret_version.secrets["auto-mystudies-urs-client-id"].secret_data
    SECRET_KEY      = data.google_secret_manager_secret_version.secrets["auto-mystudies-urs-secret-key"].secret_data
    GCP_BUCKET_NAME = "example-dev-my-studies-consent-documents"
  }
}

resource "kubernetes_secret" "study_designer_secrets" {
  metadata {
    name = "study-designer-secrets"
  }

  data = {
    CLIENT_ID  = data.google_secret_manager_secret_version.secrets["auto-mystudies-wcp-client-id"].secret_data
    SECRET_KEY = data.google_secret_manager_secret_version.secrets["auto-mystudies-wcp-secret-key"].secret_data
    BASE_URL   = "https://example-dev.example.com."
  }
}

resource "kubernetes_secret" "study_meta_data_secrets" {
  metadata {
    name = "study-meta-data-secrets"
  }

  data = {
    CLIENT_ID  = data.google_secret_manager_secret_version.secrets["auto-mystudies-wcp-client-id"].secret_data
    SECRET_KEY = data.google_secret_manager_secret_version.secrets["auto-mystudies-wcp-secret-key"].secret_data
    BASE_URL   = "https://example-dev.example.com."
  }
}

resource "kubernetes_secret" "email_credentials" {
  metadata {
    name = "email-credentials"
  }

  data = {
    email_address  = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-address"].secret_data
    email_password = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-password"].secret_data
  }
}

# gcloud keys from service accounts
resource "google_service_account_key" "apps_service_account_keys" {
  for_each = toset(local.apps)

  service_account_id = "${each.key}-gke-sa@example-dev-apps.iam.gserviceaccount.com"
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

