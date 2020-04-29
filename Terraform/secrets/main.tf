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

# This folder contains Terraform resources to for secrets stored in Google Cloud Secret Manager.

terraform {
  required_version = "~> 0.12.0"
  required_providers {
    google      = "~> 3.0"
    google-beta = "~> 3.0"
  }
  backend "gcs" {
    bucket = "heroes-hat-dev-terraform-state-08679"
    prefix = "secrets"
  }
}

locals {
  # Mapping from app codes to client id for auth server authentication.
  # Related logic is in auth-server-ws/src/main/java/com/google/cloud/healthcare/fdamystudies/controller/AuthenticationController.java.
  auth_server_apps = {
    "ma" = "test_client_id_ma" # Mobile App
    "urs" = "urs_client_id" # User Registration Server
    "rs" = "rs_client_id" # Response Server
    "wcp" = "wcp_client_id" # Web Config Portal
  }
}

resource "google_secret_manager_secret" "secrets" {
  provider = google-beta

  for_each = toset([
    "my-studies-sql-default-user-password",
    "my-studies-registration-client-id",
    "my-studies-registration-client-secret",
    "my-studies-wcp-user",
    "my-studies-wcp-pass",
    "my-studies-email-address",
    "my-studies-email-password",
  ])

  secret_id = each.key
  project   = var.project_id

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret" "app_client_id_secrets" {
  provider = google-beta

  for_each = toset(keys(local.auth_server_apps))

  secret_id = "mystudies-${each.key}-client-id"
  project   = var.project_id

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "app_client_id_secrets_values" {
  provider = google-beta

  for_each = toset(keys(local.auth_server_apps))

  secret      = google_secret_manager_secret.app_client_id_secrets[each.key].id
  secret_data = local.auth_server_apps[each.key]
}

resource "random_password" "random_secret_keys" {
  for_each = toset(keys(local.auth_server_apps))

  length  = 16
  special = true
}

resource "google_secret_manager_secret" "app_secret_key_secrets" {
  provider = google-beta

  for_each = toset(keys(local.auth_server_apps))

  secret_id = "mystudies-${each.key}-secret-key"
  project   = var.project_id

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "app_secret_key_secrets_values" {
  provider = google-beta

  for_each = toset(keys(local.auth_server_apps))

  secret      = google_secret_manager_secret.app_secret_key_secrets[each.key].id
  secret_data = random_password.random_secret_keys[each.key].result
}
