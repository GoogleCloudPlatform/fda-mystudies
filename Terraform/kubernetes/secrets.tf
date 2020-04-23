# Copyright 2020 Google Inc.
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

locals {
  apps = [
    "auth-server",
    "response-server",
    "study-designer",
    "study-meta-data",
    "user-registration",
  ]
  apps_db_names = {
    "auth-server"       = "auth_server"
    "response-server"   = "mystudies_response_server"
    "study-designer"    = "fda_hphc"
    "study-meta-data"   = "fda_hphc"
    "user-registration" = "mystudies_userregistration"
  }
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
    dbname   = local.apps_db_names[each.key]
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

  service_account_id = "projects/${var.project_id}/serviceAccounts/${each.key}-gke"
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
