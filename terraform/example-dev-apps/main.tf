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
    bucket = "example-dev-terraform-state"
    prefix = "example-dev-apps"
  }
}

# Reserve a static external IP for the Ingress.
resource "google_compute_global_address" "ingress_static_ip" {
  name         = "mystudies-ingress-ip"
  description  = "Reserved static external IP for the GKE cluster Ingress and DNS configurations."
  address_type = "EXTERNAL" # This is the default, but be explicit because it's important.
  project      = module.project.project_id
}

# ***NOTE***: First follow
# https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app
# to install the Cloud Build app and connect your GitHub repository to your Cloud project.
#
# The following content should be initially commented out if the above manual step is not completed.
# locals {
#   apps_dependencies = {
#     "study-builder"                             = ["study-builder/**"]
#     "study-datastore"                           = ["study-datastore/**"]
#     "hydra"                                     = ["hydra/**"]
#     "oauth-scim-module"                         = ["oauth-scim-module/**", "common-modules/**"]
#     "response-datastore"                        = ["response-datastore/**", "common-modules/**"]
#     "participant-datastore/consent-mgmt-module" = ["participant-datastore/consent-mgmt-module/**", "common-modules/**"]
#     "participant-datastore/user-mgmt-module"    = ["participant-datastore/user-mgmt-module/**", "common-modules/**"]
#     "participant-datastore/enroll-mgmt-module"  = ["participant-datastore/enroll-mgmt-module/**", "common-modules/**"]
#     "participant-manager-datastore"             = ["participant-manager-datastore/**", "common-modules/**"]
#     "participant-manager"                       = ["participant-manager/**"]
#   }
# }

# resource "google_cloudbuild_trigger" "server_build_triggers" {
#   for_each = locals.apps_dependencies

#   provider = google-beta
#   project  = module.project.project_id
#   name     = replace(each.key, "/", "-")
#
#   included_files = each.value
#
#   github {
#     owner = "GoogleCloudPlatform"
#     name  = "example"
#     push { branch = "^master$" }
#   }
#
#   filename = "${each.key}/cloudbuild.yaml"
# }

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google//modules/shared_vpc"
  version = "~> 9.1.0"

  name                    = "example-dev-apps"
  org_id                  = ""
  folder_id               = "0000000000"
  billing_account         = "XXXXXX-XXXXXX-XXXXXX"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "example-dev-networks"
  shared_vpc_subnets = [
    "projects/example-dev-networks/regions/us-central1/subnetworks/example-dev-gke-subnet",
  ]
  activate_apis = [
    "binaryauthorization.googleapis.com",
    "compute.googleapis.com",
    "container.googleapis.com",
    "dns.googleapis.com",
  ]
}
resource "google_binary_authorization_policy" "policy" {
  project = module.project.project_id

  # Allow Google-built images.
  # See https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#globalpolicyevaluationmode
  global_policy_evaluation_mode = "ENABLE"

  # Block all other images.
  # See https://cloud.google.com/binary-authorization/docs/policy-yaml-reference#defaultadmissionrule
  default_admission_rule {
    evaluation_mode  = "ALWAYS_DENY"
    enforcement_mode = "ENFORCED_BLOCK_AND_AUDIT_LOG"
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

  # Not all istio images are added by default in the "google images" policy.
  admission_whitelist_patterns {
    name_pattern = "gke.gcr.io/istio/*"
  }
  admission_whitelist_patterns {
    # The more generic pattern above does not seem to be enough for all images.
    name_pattern = "gke.gcr.io/istio/prometheus/*"
  }

  # Calico images in a new registry.
  admission_whitelist_patterns {
    name_pattern = "gcr.io/projectcalico-org/*"
  }
  # Whitelist images from this project.
  admission_whitelist_patterns {
    name_pattern = "gcr.io/${module.project.project_id}/*"
  }

  admission_whitelist_patterns {
    name_pattern = "gcr.io/cloudsql-docker/*"
  }
}

module "example_dev" {
  source  = "terraform-google-modules/cloud-dns/google"
  version = "~> 3.0.0"

  name       = "example-dev"
  project_id = module.project.project_id
  domain     = "example-dev.example.com."
  type       = "public"

  recordsets = [
    {
      name    = "example-dev"
      records = ["${google_compute_global_address.ingress_static_ip.address}"]
      ttl     = 30
      type    = "A"
    },
  ]

}

module "example_dev_gke_cluster" {
  source  = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster-update-variant"
  version = "~> 11.1.0"

  # Required.
  name               = "example-dev-gke-cluster"
  project_id         = module.project.project_id
  region             = "us-central1"
  regional           = true
  network_project_id = "example-dev-networks"

  network                = "example-dev-network"
  subnetwork             = "example-dev-gke-subnet"
  ip_range_pods          = "example-dev-pods-range"
  ip_range_services      = "example-dev-services-range"
  master_ipv4_cidr_block = "192.168.0.0/28"
  master_authorized_networks = [
    {
      cidr_block   = "0.0.0.0/0"
      display_name = "Example diplay name"
    },
  ]
  istio                   = true
  skip_provisioners       = true
  enable_private_endpoint = false
  release_channel         = "STABLE"

}

resource "google_service_account" "auth_server_gke_sa" {
  account_id = "auth-server-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "hydra_gke_sa" {
  account_id = "hydra-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "response_datastore_gke_sa" {
  account_id = "response-datastore-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "study_builder_gke_sa" {
  account_id = "study-builder-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "study_datastore_gke_sa" {
  account_id = "study-datastore-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "consent_datastore_gke_sa" {
  account_id = "consent-datastore-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "enroll_datastore_gke_sa" {
  account_id = "enroll-datastore-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "user_datastore_gke_sa" {
  account_id = "user-datastore-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "participant_manager_gke_sa" {
  account_id = "participant-manager-gke-sa"
  project    = module.project.project_id
}

resource "google_service_account" "triggers_pubsub_handler_gke_sa" {
  account_id = "triggers-pubsub-handler-gke-sa"
  project    = module.project.project_id
}
