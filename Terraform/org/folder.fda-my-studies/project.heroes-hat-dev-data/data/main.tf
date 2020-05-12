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

terraform {
  required_version = "~> 0.12.0"
  required_providers {
    google      = "~> 3.0"
    google-beta = "~> 3.0"
  }
  backend "gcs" {}
}

module "images_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "heroes-hat-dev-images"
  project_id = var.project_id
  location   = var.storage_location
}

module "my_studies_consent_documents_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name        = "heroes-hat-dev-my-studies-consent-documents"
  project_id  = var.project_id
  location    = var.storage_location
  iam_members = var.consent_documents_iam_members
}

module "my_studies_fda_resources_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name        = "heroes-hat-dev-my-studies-fda-resources"
  project_id  = var.project_id
  location    = var.storage_location
  iam_members = var.fda_resources_iam_members
}

module "my_studies_institution_resources_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name        = "heroes-hat-dev-my-studies-institution-resources"
  project_id  = var.project_id
  location    = var.storage_location
  iam_members = var.institution_resources_iam_members
}


module "my_studies_sql_import_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "${var.project_id}-sql-import"
  project_id = var.project_id
  location   = var.storage_location
  iam_members = [
    {
      role   = "roles/storage.objectViewer"
      member = "serviceAccount:${module.my_studies_cloudsql.instance_service_account_email_address}"
    }
  ]
}

data "google_secret_manager_secret_version" "sql_password" {
  provider = google-beta
  project  = var.secrets_project_id
  secret   = "my-studies-sql-default-user-password"
}

module "my_studies_cloudsql" {
  source  = "GoogleCloudPlatform/sql-db/google//modules/safer_mysql"
  version = "3.2.0"

  name              = "my-studies"
  project_id        = var.project_id
  region            = var.cloudsql_region
  zone              = var.cloudsql_zone
  availability_type = "REGIONAL"
  database_version  = "MYSQL_5_7"
  vpc_network       = var.network
  user_password     = data.google_secret_manager_secret_version.sql_password.secret_data

  backup_configuration = {
    enabled            = true
    binary_log_enabled = true
    start_time         = "20:55"
  }
}

# Firestore data export
module "my_studies_firestore_data_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "heroes-hat-dev-my-studies-firestore-data"
  project_id = var.project_id
  location   = var.storage_location

  # TTL 7 days.
  lifecycle_rules = [{
    action = {
      type = "Delete"
    }
    condition = {
      age        = 7 # 7 days
      with_state = "ANY"
    }
  }]

}

module "my_studies_firestore_data_bigquery" {
  source  = "terraform-google-modules/bigquery/google"
  version = "~> 4.1.0"

  dataset_id = "heroes_hat_dev_my_studies_firestore_data"
  project_id = var.project_id
  location   = var.storage_location
}
