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
    prefix = "example-dev-firebase"
  }
}

resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = module.project.project_id
}

# Step 5.1: uncomment and re-run the engine once all previous steps have been completed.
# resource "google_firestore_index" "activities_index" {
#   project    = module.project.project_id
#   collection = "Activities"
#   fields {
#     field_path = "participantId"
#     order      = "ASCENDING"
#   }
#   fields {
#     field_path = "createdTimestamp"
#     order      = "ASCENDING"
#   }
#   fields {
#     field_path = "__name__"
#     order      = "ASCENDING"
#   }
# }

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 8.1.0"

  name                    = "example-dev-firebase"
  org_id                  = ""
  folder_id               = "0000000000"
  billing_account         = "XXXXXX-XXXXXX-XXXXXX"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "firebase.googleapis.com",
  ]
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.2.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/datastore.importExportAdmin" = [
      "serviceAccount:${google_firebase_project.firebase.project}@appspot.gserviceaccount.com",
    ],
    "roles/datastore.user" = [
      "serviceAccount:response-server-gke-sa@example-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@example-dev-apps.iam.gserviceaccount.com",
    ],
    "roles/pubsub.subscriber" = [
      "serviceAccount:triggers-pubsub-handler-gke-sa@example-dev-apps.iam.gserviceaccount.com",
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

module "example_dev_my_studies_firestore_raw_data" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "example-dev-my-studies-firestore-raw-data"
  project_id = module.project.project_id
  location   = "us-central1"

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
  iam_members = [
    {
      member = "serviceAccount:${google_firebase_project.firebase.project}@appspot.gserviceaccount.com"
      role   = "roles/storage.admin"
    },
  ]
}
