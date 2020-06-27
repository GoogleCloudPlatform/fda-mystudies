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

resource "google_compute_shared_vpc_host_project" "host" {
  project = var.project_id
}

resource "google_compute_shared_vpc_service_project" "service_projects" {
  for_each        = toset([for p in var.service_projects : p.id])
  host_project    = google_compute_shared_vpc_host_project.host.project
  service_project = each.value
}

locals {
  gke_service_projects          = [for p in var.service_projects : p if p.has_gke]
  gke_service_project_id_to_num = { for p in local.gke_service_projects : p.id => p.num }
  gke_subnet                    = module.private.subnets["${var.region}/${local.gke_clusters_subnet_name}"]
}

resource "google_project_iam_member" "k8s_host_service_agent_users" {
  for_each = local.gke_service_project_id_to_num
  project  = var.project_id
  role     = "roles/container.hostServiceAgentUser"
  member   = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}

resource "google_compute_subnetwork_iam_member" "k8s_network_users" {
  for_each   = local.gke_service_project_id_to_num
  subnetwork = local.gke_subnet.id
  region     = local.gke_subnet.region
  role       = "roles/compute.networkUser"
  member     = "serviceAccount:service-${each.value}@container-engine-robot.iam.gserviceaccount.com"
}

resource "google_compute_subnetwork_iam_member" "google_apis_network_users" {
  for_each   = local.gke_service_project_id_to_num
  subnetwork = local.gke_subnet.id
  region     = local.gke_subnet.region
  role       = "roles/compute.networkUser"
  member     = "serviceAccount:${each.value}@cloudservices.gserviceaccount.com"
}
