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

# This folder contains Terraform resources to setup the Cloud Build Triggers to auto-generate Docker
# images used by the app GKE Clusters when code changes are made in the GitHub repo at configured
# branches.

# ***NOTE***: First follow
# https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app
# to install the Cloud Build app and connect your GitHub repository to your Cloud project.
#
# The following content should be initially commented out if the above manual step is not completed.

# resource "google_cloudbuild_trigger" "wcp" {
#   provider = google-beta
#   project  = var.project_id
#   name     = "wcp"

#   included_files = [
#     "WCP/**"
#   ]

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "WCP/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "auth_server_ws" {
#   provider = google-beta
#   project  = var.project_id
#   name     = "auth-server-ws"

#   included_files = [
#     "auth-server-ws/**"
#   ]

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "auth-server-ws/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "wcp_ws" {
#   provider = google-beta
#   project  = var.project_id
#   name     = "wcp-ws"

#   included_files = [
#     "WCP-WS/**"
#   ]

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "WCP-WS/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "user_registration_server_ws" {
#   provider = google-beta
#   project  = var.project_id
#   name     = "user-registration-server-ws"

#   included_files = [
#     "user-registration-server-ws/**"
#   ]

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "user-registration-server-ws/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "response_server_ws" {
#   provider = google-beta
#   project  = var.project_id
#   name     = "response-server-ws"

#   included_files = [
#     "response-server-ws/**"
#   ]

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "response-server-ws/cloudbuild.yaml"
# }
