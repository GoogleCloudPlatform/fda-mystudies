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
    bucket = "btc-terradeploy-terraform-state"
    prefix = "btc-terradeploy-firebase"
  }
}

resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = module.project.project_id
}

# Step 5.3: uncomment and re-run the engine once all previous steps have been completed.
#5# resource "google_firestore_index" "activities_index" {
#5#   project    = module.project.project_id
#5#   collection = "Activities"
#5#   fields {
#5#     field_path = "participantId"
#5#     order      = "ASCENDING"
#5#   }
#5#   fields {
#5#     field_path = "createdTimestamp"
#5#     order      = "ASCENDING"
#5#   }
#5#   fields {
#5#     field_path = "__name__"
#5#     order      = "ASCENDING"
#5#   }
#5# }

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 9.1.0"

  name                    = "btc-terradeploy-firebase"
  org_id                  = ""
  folder_id               = "341654584863"
  billing_account         = "010BB2-E7A763-738CAE"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "firebase.googleapis.com",
  ]
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.3.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/datastore.user" = [
      "serviceAccount:response-datastore-gke-sa@btc-terradeploy-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@btc-terradeploy-apps.iam.gserviceaccount.com",
    ],
    "roles/pubsub.subscriber" = [
      "serviceAccount:triggers-pubsub-handler-gke-sa@btc-terradeploy-apps.iam.gserviceaccount.com",
    ],
  }
}

module "surveyWriteTrigger" {
  source  = "terraform-google-modules/pubsub/google"
  version = "~> 1.4.0"

  topic      = "surveyWriteTrigger"
  project_id = module.project.project_id

  pull_subscriptions = [
    {
      ack_deadline_seconds = 10
      name                 = "surveyWriteGlobal"
    },
  ]
}

resource "google_service_account" "raw_data_export" {
  account_id = "raw-data-export"
  project    = module.project.project_id
}

resource "google_service_account" "bigquery_export" {
  account_id = "bigquery-export"
  project    = module.project.project_id
}

resource "google_service_account" "real_time_triggers" {
  account_id = "real-time-triggers"
  project    = module.project.project_id
}

module "btc_terradeploy_mystudies_firestore_raw_data" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "btc-terradeploy-mystudies-firestore-raw-data"
  project_id = module.project.project_id
  location   = "us-east1"

  lifecycle_rules = [
    {
      action = {
        type = "Delete"
      }
      condition = {
        age        = 7
        with_state = "ANY"
      }
    }
  ]
}
