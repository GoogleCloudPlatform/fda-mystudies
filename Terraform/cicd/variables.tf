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

variable "org_id" {
  description = "GCP Organization ID that Cloud Build Service Account will have purview over"
  type        = string
}

variable "devops_project_id" {
  description = "Project ID of the devops project to create the Cloud Build Triggers"
  type        = string
}

variable "state_bucket" {
  description = "Name of the Terraform state bucket"
  type        = string
}

variable "repo_owner" {
  description = "Owner of the GitHub repo"
  type        = string
}

variable "repo_name" {
  description = "Name of the GitHub repo"
  type        = string
}

variable "cloudbuild_trigger_branch" {
  description = "Name of the branch to set the Cloud Build Triggers to watch for"
  type        = string
}

variable "continuous_deployment_enabled" {
  description = "Whether or not to enable continuous deployment of Terraform configs"
  type        = bool
  default     = false
}

variable "trigger_enabled" {
  description = "Whether or not to enable all Cloud Build Triggers"
  type        = bool
  default     = true
}
