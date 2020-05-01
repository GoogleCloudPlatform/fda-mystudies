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
  required_version = "~> 0.12.0"
  required_providers {
    google      = "~> 3.0"
    google-beta = "~> 3.0"
  }
  backend "gcs" {}
}

# Network values are defined the same way in the network component.

# From
# https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/modules/safer-cluster-update-variant
#module "heroes_hat_cluster" {
#  source  = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster"
#  version = "8.1.0"
#
#  # Required
#  # TODO: Set release_channel to "regular" when https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/pull/487 is released.
#  name                   = var.cluster_name
#  project_id             = var.project_id
#  region                 = var.gke_region
#  regional               = true
#  network_project_id     = var.network_project_id
#  network                = var.network
#  subnetwork             = var.subnetwork
#  ip_range_pods          = "heroes-hat-cluster-ip-range-pods"
#  ip_range_services      = "heroes-hat-cluster-ip-range-svc"
#  master_ipv4_cidr_block = "192.168.0.0/28"
#
#  # Optional
#  # Some of these were taken from the example config at
#  # https://github.com/terraform-google-modules/terraform-google-kubernetes-engine/tree/master/examples/safer_cluster
#  istio             = true
#  skip_provisioners = true
#
#  # Configure master auth networks.
#  # Private endpoint must be disabled, otherwise the master is only accessible
#  # via a Cloud Interconnect or Cloud VPN.
#  # This allows access over the internet, but only from certain source ranges.
#  enable_private_endpoint    = false
#  master_authorized_networks = var.master_authorized_networks
#}

module "heroes_hat_cluster" {
  source             = "terraform-google-modules/kubernetes-engine/google//modules/beta-private-cluster-update-variant"
  version = "8.1.0"


  name                   = var.cluster_name
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

  # Configure master auth networks.
  # Private endpoint must be disabled, otherwise the master is only accessible
  # via a Cloud Interconnect or Cloud VPN.
  # This allows access over the internet, but only from certain source ranges.
  enable_private_endpoint    = false
  master_authorized_networks = var.master_authorized_networks

  zones              = []


  // We need to enforce a minimum Kubernetes Version to ensure
  // that the necessary security features are enabled.
  kubernetes_version = "1.14.10-gke.27"

  // Nodes are created with a default version. The nodepool enables
  // auto_upgrade so that the node versions can be kept up to date with
  // the master upgrades.
  //
  // https://cloud.google.com/kubernetes-engine/versioning-and-upgrades
  node_version = ""

  horizontal_pod_autoscaling = true
  http_load_balancing        = true

  // We suggest the use coarse network policies to enforce restrictions in the
  // communication between pods.
  //
  // NOTE: Enabling network policy is not sufficient to enforce restrictions.
  // NetworkPolicies need to be configured in every namespace. The network
  // policies should be under the control of a cental cluster management team,
  // rather than individual teams.
  network_policy = false

  maintenance_start_time = "05:00"

  initial_node_count = 0

  // We suggest removing the default node pull, as it cannot be modified without
  // destroying the cluster.
  remove_default_node_pool = true

  node_pools          = var.node_pools
  node_pools_labels   = var.node_pools_labels
  node_pools_metadata = var.node_pools_metadata
  node_pools_taints   = var.node_pools_taints
  node_pools_tags     = var.node_pools_tags

  node_pools_oauth_scopes = var.node_pools_oauth_scopes

  stub_domains         = {}
  upstream_nameservers = []

  logging_service    = "logging.googleapis.com/kubernetes"
  monitoring_service = "monitoring.googleapis.com/kubernetes"

  // We never use the default service account for the cluster. The default
  // project/editor permissions can create problems if nodes were to be ever
  // compromised.

  // We either:
  // - Create a dedicated service account with minimal permissions to run nodes.
  //   All applications shuold run with an identity defined via Workload Identity anyway.
  // - Use a service account passed as a parameter to the module, in case the user
  //   wants to maintain control of their service accounts.
  create_service_account = true
  service_account        = ""
  registry_project_id    = ""
  grant_registry_access  = true

  // Basic Auth disabled
  basic_auth_username = ""
  basic_auth_password = ""

  issue_client_certificate = false

  cluster_resource_labels = {}

  // We enable private endpoints to limit exposure.
  deploy_using_private_endpoint = true

  // Private nodes better control public exposure, and reduce
  // the ability of nodes to reach to the Internet without
  // additional configurations.
  enable_private_nodes = true

  // Istio is recommended for pod-to-pod communications.
  istio_auth = "AUTH_MUTUAL_TLS"

  cloudrun = false

  dns_cache = false

  default_max_pods_per_node = 110

  database_encryption = [{
    state    = "DECRYPTED"
    key_name = ""
  }]

  // We suggest to define policies about  which images can run on a cluster.
  enable_binary_authorization = true

  // Define PodSecurityPolicies for differnet applications.
  // Example: https://kubernetes.io/docs/concepts/policy/pod-security-policy/#example
  pod_security_policy_config = [{
    "enabled" = true
  }]

  resource_usage_export_dataset_id = ""

  // Sandbox is needed if the cluster is going to run any untrusted workload (e.g., user submitted code).
  // Sandbox can also provide increased protection in other cases, at some performance cost.
  sandbox_enabled = false

  // Intranode Visibility enables you to capture flow logs for traffic between pods and create FW rules that apply to traffic between pods.
  enable_intranode_visibility = false

  enable_vertical_pod_autoscaling = false

  // We enable identity namespace by default.
  identity_namespace = "${var.project_id}.svc.id.goog"

  authenticator_security_group = null

  enable_shielded_nodes = true
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
  project = var.project_id

  # Whitelist images from this project.
  admission_whitelist_patterns {
    name_pattern = "gcr.io/${var.project_id}/*"
  }
  admission_whitelist_patterns {
    name_pattern = "gcr.io/cloudsql-docker/*"
  }

  # Not all istio images are added by default in the "google images" policy.
  admission_whitelist_patterns {
    name_pattern = "gke.gcr.io/istio/*"
  }
  admission_whitelist_patterns {
    # The more generic pattern above does not seem to be enough for all images.
    name_pattern = "gke.gcr.io/istio/prometheus/*"
  }

  # Recommendations from https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#admissionwhitelistpatterns
  admission_whitelist_patterns {
    name_pattern = "gcr.io/google_containers/*"
  }
  admission_whitelist_patterns {
    name_pattern = "gcr.io/google-containers/*"
  }
  admission_whitelist_patterns {
    name_pattern = "k8s.gcr.io/*"
  }
  admission_whitelist_patterns {
    name_pattern = "gke.gcr.io/*"
  }
  admission_whitelist_patterns {
    name_pattern = "gcr.io/stackdriver-agents/*"
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
