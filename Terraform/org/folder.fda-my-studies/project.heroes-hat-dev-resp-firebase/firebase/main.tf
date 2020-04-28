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

# This folder contains Terraform resources to setup a Google Cloud Firebase instance. It enables
# Firebase resources on the given GCP project.

terraform {
  required_version = "~> 0.12.0"
  required_providers {
    google = "~> 3.0"
  }
  backend "gcs" {}
}

resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = var.project_id
}
