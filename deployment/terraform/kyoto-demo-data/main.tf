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
    bucket = "kyoto-demo-terraform-state"
    prefix = "kyoto-demo-data"
  }
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google//modules/shared_vpc"
  version = "~> 9.1.0"

  name                    = "kyoto-demo-data"
  org_id                  = ""
  folder_id               = "249868370510"
  billing_account         = "010908-0509D9-5699ED"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "kyoto-demo-networks"
  activate_apis = [
    "bigquery.googleapis.com",
    "compute.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
  ]
}

module "kyoto_demo_mystudies_firestore_data" {
  source  = "terraform-google-modules/bigquery/google"
  version = "~> 4.3.0"

  dataset_id = "kyoto_demo_mystudies_firestore_data"
  project_id = module.project.project_id
  location   = "us-east1"
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.3.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/cloudsql.client" = [
      "serviceAccount:bastion@kyoto-demo-networks.iam.gserviceaccount.com",
      "serviceAccount:auth-server-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:hydra-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:response-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:study-builder-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:study-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:consent-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:enroll-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:user-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:participant-manager-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com",
    ],
  }
}

module "kyoto_demo_mystudies_consent_documents" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "kyoto-demo-mystudies-consent-documents"
  project_id = module.project.project_id
  location   = "asia-northeast1"

  iam_members = [
    {
      member = "serviceAccount:consent-datastore-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
    {
      member = "serviceAccount:participant-manager-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "kyoto_demo_mystudies_study_resources" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "kyoto-demo-mystudies-study-resources"
  project_id = module.project.project_id
  location   = "asia-northeast1"

  iam_members = [
    {
      member = "serviceAccount:study-builder-gke-sa@kyoto-demo-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
    {
      member = "allUsers"
      role   = "roles/storage.objectViewer"
    },
  ]
}

module "kyoto_demo_mystudies_sql_import" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "kyoto-demo-mystudies-sql-import"
  project_id = module.project.project_id
  location   = "asia-northeast1"

}
