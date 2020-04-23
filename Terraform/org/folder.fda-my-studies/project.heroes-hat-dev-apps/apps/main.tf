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

terraform {
  backend "gcs" {}
}

# Network values are defined the same way in the network component.

# From
# https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/modules/safer-cluster-update-variant
module "heroes_hat_cluster" {
  source = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"

  # Required
  name = "heroes-hat-cluster"
  # TODO: Set release_channel to "regular" when https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/pull/487 is merged.
  project_id             = var.project_id
  region                 = var.gke_region
  regional               = true
  network_project_id     = var.network_project_id
  network                = var.network
  subnetwork             = var.subnetwork
  ip_range_pods          = "heroes-hat-cluster-ip-range-pods"
  ip_range_services      = "heroes-hat-cluster-ip-range-svc"
  master_ipv4_cidr_block = "192.168.0.0/28"

  # Optional
  # Some of these were taken from the example config at
  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
  istio             = true
  skip_provisioners = true

  # Need to either disable private endpoint, or enable master auth networks.
  enable_private_endpoint = false
}

# Create a separate service account for each app.
locals {
  apps = [
    "auth-server",
    "response-server",
    "study-designer",
    "study-meta-data",
    "user-registration",
  ]
}

resource "google_service_account" "apps_service_accounts" {
  for_each = toset(local.apps)

  account_id  = "${each.key}-gke"
  description = "Terraform-generated service account for use by the ${each.key} GKE app"
  project     = var.project_id
}

# Reserve a static external IP for the Ingress.
resource "google_compute_global_address" "ingress_static_ip" {
  name         = "my-studies-ingress-ip"
  description  = "Reserved static external IP for the GKE cluster Ingress and DNS configurations."
  address_type = "EXTERNAL" # This is the default, but be explicit because it's important.
  project      = var.project_id
}

# Binary Authorization resources.
# Simple configuration for now. Future
# See https://cloud.google.com/binary-authorization/docs/overview
resource "google_binary_authorization_policy" "policy" {
  # Whitelist images from this project.
  # See https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#admissionwhitelistpatterns
  admission_whitelist_patterns {
    name_pattern = "gcr.io/${var.project_id}/*"
  }

  # Allow Google-built images.
  # See https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#globalpolicyevaluationmode
  global_policy_evaluation_mode = "ENABLE"

  # Block all non-whitelisted images.
  # See https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#defaultadmissionrule
  default_admission_rule {
    evaluation_mode  = "ALWAYS_DENY"
    enforcement_mode = "ENFORCED_BLOCK_AND_AUDIT_LOG"
  }
}
