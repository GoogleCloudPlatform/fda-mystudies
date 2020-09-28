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
    prefix = "example-dev-data"
  }
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 8.1.0"

  name                    = "example-dev-data"
  org_id                  = ""
  folder_id               = "0000000000"
  billing_account         = "XXXXXX-XXXXXX-XXXXXX"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "example-dev-networks"
  activate_apis = [
    "bigquery.googleapis.com",
    "compute.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
  ]
}

module "example_dev_my_studies_firestore_data" {
  source  = "terraform-google-modules/bigquery/google"
  version = "~> 4.3.0"

  dataset_id = "example_dev_my_studies_firestore_data"
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
      "serviceAccount:example-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/bigquery.jobUser" = [
      "serviceAccount:example-dev-firebase@appspot.gserviceaccount.com",
    ],
    "roles/cloudsql.client" = [
      "serviceAccount:bastion@example-dev-networks.iam.gserviceaccount.com",
      "serviceAccount:auth-server-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:response-server-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-designer-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:study-metadata-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:user-registration-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:participant-manager-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@example-dev-apps.iam.gserviceaccount.com",
    ],
  }
}

module "example_dev_mystudies_consent_documents" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-dev-mystudies-consent-documents"
  project_id = module.project.project_id
  location   = "us-central1"

  iam_members = [
    {
      member = "serviceAccount:user-registration-gke-sa@example-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
    {
      member = "serviceAccount:participant-manager-gke-sa@example-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "example_dev_mystudies_fda_resources" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-dev-mystudies-fda-resources"
  project_id = module.project.project_id
  location   = "us-central1"

  iam_members = [
    {
      member = "serviceAccount:study-designer-gke-sa@example-dev-apps.iam.gserviceaccount.com"
      role   = "roles/storage.objectAdmin"
    },
  ]
}

module "example_dev_mystudies_sql_import" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-dev-mystudies-sql-import"
  project_id = module.project.project_id
  location   = "us-central1"

}
