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
    bucket = "btcsoft-dev-terraform-state"
    prefix = "btcsoft-dev-firebase"
  }
}

resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = module.project.project_id
}

# Step 5.3: uncomment and re-run the engine once all previous steps have been completed.
resource "google_firestore_index" "activities_index" {
  project    = module.project.project_id
  collection = "Activities"
  fields {
    field_path = "participantId"
    order      = "ASCENDING"
  }
  fields {
    field_path = "createdTimestamp"
    order      = "ASCENDING"
  }
  fields {
    field_path = "__name__"
    order      = "ASCENDING"
  }
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 9.1.0"

  name                    = "btcsoft-dev-firebase"
  org_id                  = ""
  folder_id               = "274914618000"
  billing_account         = "010BB2-E7A763-738CAE"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "firebase.googleapis.com",
    "healthcare.googleapis.com",
  ]
}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.3.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/datastore.importExportAdmin" = [
      "serviceAccount:${google_firebase_project.firebase.project}@appspot.gserviceaccount.com",
    ],
    "roles/datastore.user" = [
      "serviceAccount:response-datastore-gke-sa@btcsoft-dev-apps.iam.gserviceaccount.com",
      "serviceAccount:triggers-pubsub-handler-gke-sa@btcsoft-dev-apps.iam.gserviceaccount.com",
    ],
    "roles/pubsub.subscriber" = [
      "serviceAccount:triggers-pubsub-handler-gke-sa@btcsoft-dev-apps.iam.gserviceaccount.com",
    ],
    "roles/healthcare.fhirResourceEditor" = [
      "serviceAccount:response-datastore-gke-sa@btcsoft-dev-apps.iam.gserviceaccount.com",
    ],
    "roles/healthcare.fhirStoreAdmin" = [
      "serviceAccount:response-datastore-gke-sa@btcsoft-dev-apps.iam.gserviceaccount.com",
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

module "btcsoft_dev_mystudies_firestore_raw_data" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = "btcsoft-dev-mystudies-firestore-raw-data"
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
  iam_members = [
    {
      member = "serviceAccount:${google_firebase_project.firebase.project}@appspot.gserviceaccount.com"
      role   = "roles/storage.admin"
    },
  ]
}


resource "google_healthcare_fhir_store" "default" {
  name    = "1402"
  dataset = google_healthcare_dataset.dataset.id
  version = "R4"

  enable_update_create          = false
  disable_referential_integrity = false
  disable_resource_versioning   = false
  enable_history_import         = false

  stream_configs {
    resource_types = ["Observation"]
    bigquery_destination {
      dataset_uri = "bq://${google_bigquery_dataset.bq_dataset.project}.${google_bigquery_dataset.bq_dataset.dataset_id}"
      schema_config {
        recursive_structure_depth = 3
      }
    }
  }
}

module "fhir-notifications" {
  source  = "terraform-google-modules/pubsub/google"
  version = "~> 1.4.0"

  topic      = "fhir-notifications"
  project_id = module.project.project_id

  pull_subscriptions = [
    {
      ack_deadline_seconds = 10
      name                 = "fhir-notifications"
    },
  ]

}
resource "google_pubsub_topic" "fhir-notifications" {
  name = "fhir-notifications"
}

resource "google_healthcare_dataset" "FHIR-Response" {
  name     = "FHIR-Response"
  location = "us-central1"
}

resource "google_bigquery_dataset" "MyStudies" {
  dataset_id                 = "MyStudies"
  description                = "This is a test description"
  location                   = "us-central1"
  delete_contents_on_destroy = true
}
