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
  version                = "1.13.3"
  load_config_file       = false
  token                  = data.google_client_config.default.access_token
  host                   = data.google_container_cluster.gke_cluster.endpoint
  client_certificate     = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_certificate)
  client_key             = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_key)
  cluster_ca_certificate = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.cluster_ca_certificate)
}

locals {
  # hydra is treated separately.
  apps = [
    "auth-server",
    "response-datastore",
    "study-builder",
    "study-datastore",
    "participant-consent-datastore",
    "participant-enroll-datastore",
    "participant-user-datastore",
    "participant-manager-datastore",
  ]
  apps_db_names = {
    "auth-server"                   = "oauth_server_hydra"
    "response-datastore"            = "mystudies_response_server"
    "study-builder"                 = "fda_hphc"
    "study-datastore"               = "fda_hphc"
    "participant-consent-datastore" = "mystudies_participant_datastore"
    "participant-enroll-datastore"  = "mystudies_participant_datastore"
    "participant-user-datastore"    = "mystudies_participant_datastore"
    "participant-manager-datastore" = "mystudies_participant_datastore"
  }
  service_account_ids = [
    "auth-server-gke-sa",
    "hydra-gke-sa",
    "response-datastore-gke-sa",
    "study-builder-gke-sa",
    "study-datastore-gke-sa",
    "consent-datastore-gke-sa",
    "enroll-datastore-gke-sa",
    "user-datastore-gke-sa",
    "participant-manager-gke-sa",
  ]
}

# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "secrets" {
  provider = google-beta
  project  = "example-dev-secrets"
  secret   = each.key

  for_each = toset(concat(
    [
      "manual-mystudies-email-address",
      "manual-mystudies-email-password",
      "manual-mystudies-contact-email-address",
      "manual-mystudies-from-email-address",
      "manual-mystudies-from-email-domain",
      "manual-mystudies-smtp-hostname",
      "manual-mystudies-smtp-use-ip-allowlist",
      "manual-log-path",
      "manual-org-name",
      "manual-terms-url",
      "manual-privacy-url",
      "manual-fcm-api-url",
      "manual-ios-deeplink-url",
      "manual-android-deeplink-url",
      "auto-auth-server-encryptor-password",
      "auto-hydra-db-password",
      "auto-hydra-db-user",
      "auto-hydra-system-secret",
      "auto-sd-response-datastore-token",
      "auto-sd-response-datastore-id",
      "auto-sd-android-token",
      "auto-sd-android-id",
      "auto-sd-ios-token",
      "auto-sd-ios-id",
    ],
    formatlist("auto-%s-db-user", local.apps),
    formatlist("auto-%s-db-password", local.apps),
    formatlist("auto-%s-client-id", local.apps),
    formatlist("auto-%s-secret-key", local.apps))
  )
}

# Shared secrets.
resource "kubernetes_secret" "shared_secrets" {
  metadata {
    name = "shared-secrets"
  }

  data = {
    consent_bucket_name               = "example-dev-mystudies-consent-documents"
    study_resources_bucket_name       = "example-dev-mystudies-study-resources"
    study_export_import_bucket_name   = "example-dev-mystudies-sql-import"
    institution_resources_bucket_name = "example-dev-mystudies-institution-resources"
    base_url                          = "https://participants.example-dev.example.com"
    studies_base_url                  = "https://studies.example-dev.example.com"
    firestore_project_id              = "example-dev-firebase"
    log_path                          = data.google_secret_manager_secret_version.secrets["manual-log-path"].secret_data
    org_name                          = data.google_secret_manager_secret_version.secrets["manual-org-name"].secret_data
    terms_url                         = data.google_secret_manager_secret_version.secrets["manual-terms-url"].secret_data
    privacy_url                       = data.google_secret_manager_secret_version.secrets["manual-privacy-url"].secret_data
    fcm_api_url                       = data.google_secret_manager_secret_version.secrets["manual-fcm-api-url"].secret_data
  }
}

# App credentials.
resource "kubernetes_secret" "apps_credentials" {
  for_each = toset(local.apps)

  metadata {
    name = "${each.key}-credentials"
  }

  data = {
    dbusername = data.google_secret_manager_secret_version.secrets["auto-${each.key}-db-user"].secret_data
    dbpassword = data.google_secret_manager_secret_version.secrets["auto-${each.key}-db-password"].secret_data
    client_id  = data.google_secret_manager_secret_version.secrets["auto-${each.key}-client-id"].secret_data
    secret_key = data.google_secret_manager_secret_version.secrets["auto-${each.key}-secret-key"].secret_data
    dbname     = local.apps_db_names[each.key]
  }
}

# Client-side credentials.
resource "kubernetes_secret" "client_side_credentials" {

  metadata {
    name = "client-side-credentials"
  }

  data = {
    client_id  = data.google_secret_manager_secret_version.secrets["auto-auth-server-client-id"].secret_data
    secret_key = data.google_secret_manager_secret_version.secrets["auto-auth-server-secret-key"].secret_data
  }
}

# Auth-server secrets.
resource "kubernetes_secret" "auth_server_secrets" {

  metadata {
    name = "auth-server-secrets"
  }

  data = {
    encryptor_password   = data.google_secret_manager_secret_version.secrets["auto-auth-server-encryptor-password"].secret_data
    ios_deeplink_url     = data.google_secret_manager_secret_version.secrets["manual-ios-deeplink-url"].secret_data
    android_deeplink_url = data.google_secret_manager_secret_version.secrets["manual-android-deeplink-url"].secret_data
  }
}


# Hydra credentials.
resource "kubernetes_secret" "hydra_credentials" {

  metadata {
    name = "hydra-credentials"
  }

  data = {
    dbusername    = data.google_secret_manager_secret_version.secrets["auto-hydra-db-user"].secret_data
    dbpassword    = data.google_secret_manager_secret_version.secrets["auto-hydra-db-password"].secret_data
    system_secret = data.google_secret_manager_secret_version.secrets["auto-hydra-system-secret"].secret_data
    dbname        = "hydra"
  }
}

# Study datastore connect credentials.
resource "kubernetes_secret" "study_datastore_connect_credentials" {
  metadata {
    name = "study-datastore-connect-credentials"
  }
  data = {
    response_datastore_id    = data.google_secret_manager_secret_version.secrets["auto-sd-response-datastore-id"].secret_data
    response_datastore_token = data.google_secret_manager_secret_version.secrets["auto-sd-response-datastore-token"].secret_data
    android_id               = data.google_secret_manager_secret_version.secrets["auto-sd-android-id"].secret_data
    android_token            = data.google_secret_manager_secret_version.secrets["auto-sd-android-token"].secret_data
    ios_id                   = data.google_secret_manager_secret_version.secrets["auto-sd-ios-id"].secret_data
    ios_token                = data.google_secret_manager_secret_version.secrets["auto-sd-ios-token"].secret_data
  }
}

# Email credentials.
resource "kubernetes_secret" "email_credentials" {
  metadata {
    name = "email-credentials"
  }

  data = {
    email_address         = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-address"].secret_data
    email_password        = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-password"].secret_data
    contact_email_address = data.google_secret_manager_secret_version.secrets["manual-mystudies-contact-email-address"].secret_data
    from_email_address    = data.google_secret_manager_secret_version.secrets["manual-mystudies-from-email-address"].secret_data
    from_email_domain     = data.google_secret_manager_secret_version.secrets["manual-mystudies-from-email-domain"].secret_data
    smtp_hostname         = data.google_secret_manager_secret_version.secrets["manual-mystudies-smtp-hostname"].secret_data
    smtp_use_ip_allowlist = data.google_secret_manager_secret_version.secrets["manual-mystudies-smtp-use-ip-allowlist"].secret_data
  }
}

# gcloud keys from service accounts
resource "google_service_account_key" "apps_service_account_keys" {
  for_each = toset(local.service_account_ids)

  service_account_id = "${each.key}@example-dev-apps.iam.gserviceaccount.com"
}

resource "kubernetes_secret" "apps_gcloud_keys" {
  for_each = toset(local.service_account_ids)

  metadata {
    name = "${each.key}-gcloud-key"
  }
  data = {
    "key.json" = base64decode(google_service_account_key.apps_service_account_keys[each.key].private_key)
  }
}

