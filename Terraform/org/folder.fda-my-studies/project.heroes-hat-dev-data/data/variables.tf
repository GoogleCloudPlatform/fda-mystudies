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

variable "project_id" {
  type = string
}

variable "secrets_project_id" {
  type = string
}

variable "network" {
  type = string
}

variable "storage_location" {
  type = string
}

variable "cloudsql_region" {
  type = string
}

variable "cloudsql_zone" {
  type = string
}

variable "consent_documents_iam_members" {
  type = list(object({
    role   = string
    member = string
  }))
  default = []
}

variable "fda_resources_iam_members" {
  type = list(object({
    role   = string
    member = string
  }))
  default = []
}

variable "institution_resources_iam_members" {
  type = list(object({
    role   = string
    member = string
  }))
  default = []
}

