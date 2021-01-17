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
    bucket = "fms-dev-terraform-state"
    prefix = "fms-dev-data"
  }
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google//modules/shared_vpc"
  version = "~> 9.1.0"

  name                    = "fms-dev-data"
  org_id                  = ""
  folder_id               = "562423384667"
  billing_account         = "0173D6-DA0244-B4E985"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "fms-dev-networks"
  activate_apis = [
    "bigquery.googleapis.com",
    "compute.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
  ]
}

module "fms_dev_mystudies_firestore_data" {
  source  = "terraform-google-modules/bigquery/google"
  version = "~> 4.3.0"

  dataset_id = "fms_dev_mystudies_firestore_data"
  project_id = module.project.project_id
  location   = "us-east1"
}

module "mystudies" {
  source  = "GoogleCloudPlatform/sql-db/google//modules/safer_mysql"
  version = "~> 4.1.0"

  name              = "mystudies"
  project_id        = module.project.project_id
  region            = "us-central1"
  zone              = "a"
  availability_type = "REGIONAL"
  database_version  = "MYSQL_5_7"
  vpc_network       = "projects/example-dev-networks/global/networks/example-dev-network"
  user_password     = data.google_secret_manager_secret_version.db_secrets["auto-mystudies-sql-default-user-password"].secret_data
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.3.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/bigquery.dataEditor" = [
      "serviceAccount:example-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/bigquery.jobUser" = [
      "serviceAccount:example-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/cloudsql.client" = [
      "serviceAccount:bastion@fms-dev-networks.iam.gserviceaccount.com",
      "serviceAccount:auth-server-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:hydra-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:response-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-builder-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:consent-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:enroll-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:user-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:participant-manager-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@fms-dev-apps.iam.gserviceaccount.com",
    ],
  }
}

module "fms_dev_mystudies_consent_documents" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fms-dev-mystudies-consent-documents"
  project_id = module.project.project_id
  location   = "asia-northeast1"

  iam_members = [
    {
      member = "serviceAccount:consent-datastore-gke-sa@fms-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
    {
      member = "serviceAccount:participant-manager-gke-sa@fms-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "fms_dev_mystudies_study_resources" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fms-dev-mystudies-study-resources"
  project_id = module.project.project_id
  location   = "asia-northeast1"

  iam_members = [
    {
      member = "serviceAccount:study-builder-gke-sa@fms-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
    {
      member = "allUsers"
      role   = "roles/storage.objectViewer"
    },
  ]
}

module "fms_dev_mystudies_sql_import" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fms-dev-mystudies-sql-import"
  project_id = module.project.project_id
  location   = "asia-northeast1"

}
