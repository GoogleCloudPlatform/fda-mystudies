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

# This folder contains Terraform resources to setup the basis of the firebase project, which includes:
# - The project itself,
# - APIs to enable,
# - Deletion lien, if enabled,
# - Project level IAM permissions for the project owners.

terraform {
  backend "gcs" {}
}

module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 7.0"

  name                    = var.name
  org_id                  = var.org_id
  folder_id               = var.folder_id
  billing_account         = var.billing_account
  lien                    = var.enable_lien
  activate_apis           = var.apis
  default_service_account = "keep"
  skip_gcloud_download    = true
}

resource "google_project_iam_binding" "owners" {
  project = module.project.project_id
  role    = "roles/owner"
  members = var.owners
}
