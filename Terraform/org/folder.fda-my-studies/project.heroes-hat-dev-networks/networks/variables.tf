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

variable "region" {
  description = "The region where the network and subnets will be created for the GKE clusters and bastion host"
  type        = string
}

variable "zone" {
  description = "The zone where to create the bastion host"
  type        = string
}

variable "gke_network_name" {
  description = "The name of the network that'll be used for the GKE clusters"
  type        = string
}

variable "service_projects" {
  type = list(object({
    id : string
    num : number
    has_gke : bool
  }))
}

variable "bastion_users" {
  description = "List of IAM resources to allow access to the bastion VM instance"
  type        = list(string)
  default     = []
}
