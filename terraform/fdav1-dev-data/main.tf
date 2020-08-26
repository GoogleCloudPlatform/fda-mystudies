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
    bucket = "fdav1-dev-terraform-state"
    prefix = "fdav1-dev-data"
  }
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 8.1.0"

  name                    = "fdav1-dev-data"
  org_id                  = ""
  folder_id               = "701269119189"
  billing_account         = "01BF92-0D3888-EEBAA6"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "fdav1-dev-networks"
  activate_apis = [
    "bigquery.googleapis.com",
    "compute.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
  ]
}

module "fdav1_dev_my_studies_firestore_data" {
  source  = "terraform-google-modules/bigquery/google"
  version = "~> 4.3.0"

  dataset_id = "fdav1_dev_my_studies_firestore_data"
  project_id = module.project.project_id
  location   = "us-east1"
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.2.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/bigquery.dataEditor" = [
      "serviceAccount:fdav1-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/bigquery.jobUser" = [
      "serviceAccount:fdav1-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/cloudsql.client" = [
      "serviceAccount:bastion@fdav1-dev-networks.iam.gserviceaccount.com",
      "serviceAccount:auth-server-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:response-server-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-designer-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-metadata-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:user-registration-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com",
    ],
  }
}

module "fdav1_dev_my_studies_consent_documents" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fdav1-dev-my-studies-consent-documents"
  project_id = module.project.project_id
  location   = "us-central1"

  iam_members = [
    {
      member = "serviceAccount:user-registration-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "fdav1_dev_my_studies_fda_resources" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fdav1-dev-my-studies-fda-resources"
  project_id = module.project.project_id
  location   = "us-central1"

  iam_members = [
    {
      member = "serviceAccount:study-designer-gke-sa@fdav1-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "fdav1_dev_my_studies_sql_import" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "fdav1-dev-my-studies-sql-import"
  project_id = module.project.project_id
  location   = "us-central1"

}
