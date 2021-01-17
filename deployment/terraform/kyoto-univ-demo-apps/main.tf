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
    bucket = "kyoto-univ-demo-terraform-state"
    prefix = "kyoto-univ-demo-apps"
  }
}

# Reserve a static external IP for the Ingress.
resource "google_compute_global_address" "ingress_static_ip" {
  name         = "kyoto-univ-demo-ingress-ip"
  description  = "Reserved static external IP for the GKE cluster Ingress and DNS configurations."
  address_type = "EXTERNAL" # This is the default, but be explicit because it's important.
  project      = module.project.project_id
}

# Step 7: Cloud Build for Apps Project
# ***NOTE***: First follow
# https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app
# to install the Cloud Build app and connect your GitHub repository to your Cloud project.
#
# The following content should be initially commented out if the above manual step is not completed.
#7# locals {
#7#   apps_dependencies = {
#7#     "study-builder"                             = ["study-builder/**"]
#7#     "study-datastore"                           = ["study-datastore/**"]
#7#     "hydra"                                     = ["hydra/**"]
#7#     "auth-server"                               = ["auth-server/**", "common-modules/**"]
#7#     "response-datastore"                        = ["response-datastore/**", "common-modules/**"]
#7#     "participant-datastore/consent-mgmt-module" = ["participant-datastore/consent-mgmt-module/**", "common-modules/**"]
#7#     "participant-datastore/user-mgmt-module"    = ["participant-datastore/user-mgmt-module/**", "common-modules/**"]
#7#     "participant-datastore/enroll-mgmt-module"  = ["participant-datastore/enroll-mgmt-module/**", "common-modules/**"]
#7#     "participant-manager-datastore"             = ["participant-manager-datastore/**", "common-modules/**"]
#7#     "participant-manager"                       = ["participant-manager/**"]
#7#   }
#7# }

#7# resource "google_cloudbuild_trigger" "server_build_triggers" {
#7#   for_each = local.apps_dependencies

#7#   provider = google-beta
#7#   project  = module.project.project_id
#7#   name     = replace(each.key, "/", "-")

#7#   included_files = each.value
#7#
#7#   github {
#7#     owner = "clipcrow"
#7#     name  = "fda-mystudies"
#7#     push { branch = "^master$" }
#7#   }

#7#   filename = "${each.key}/cloudbuild.yaml"
#7# }

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google//modules/shared_vpc"
  version = "~> 9.1.0"

  name                    = "kyoto-univ-demo-apps"
  org_id                  = ""
  folder_id               = "79101201507"
  billing_account         = "010908-0509D9-5699ED"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  shared_vpc              = "kyoto-univ-demo-networks"
  shared_vpc_subnets = [
    "projects/kyoto-univ-demo-networks/regions/asia-northeast1/subnetworks/kyoto-univ-demo-gke-subnet",
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

module "kyoto_univ_demo" {
  source  = "terraform-google-modules/cloud-dns/google"
  version = "~> 3.0.0"

  name       = "kyoto-univ-demo"
  project_id = module.project.project_id
  domain     = "kyoto-univ-demo.clipcrow.com."
  type       = "public"

  recordsets = [
    {
      name    = "participants"
      records = ["${google_compute_global_address.ingress_static_ip.address}"]
      ttl     = 30
      type    = "A"
    },
    {
      name    = "studies"
      records = ["${google_compute_global_address.ingress_static_ip.address}"]
      ttl     = 30
      type    = "A"
    },
  ]
}

module "kyoto_univ_demo_gke_cluster" {
  source  = "terraform-google-modules/kubernetes-engine/google//modules/safer-cluster-update-variant"
  version = "~> 11.1.0"

  # Required.
  name               = "kyoto-univ-demo-gke-cluster"
  project_id         = module.project.project_id
  region             = "asia-northeast1"
  regional           = true
  network_project_id = "kyoto-univ-demo-networks"

  network                = "kyoto-univ-demo-network"
  subnetwork             = "kyoto-univ-demo-gke-subnet"
  ip_range_pods          = "kyoto-univ-demo-pods-range"
  ip_range_services      = "kyoto-univ-demo-services-range"
  master_ipv4_cidr_block = "192.168.0.0/28"
  master_authorized_networks = [
    {
      cidr_block   = "0.0.0.0/0"
      display_name = "clipcrow-terraform-deploy-ip"
    },
  ]
  istio                   = true
  skip_provisioners       = true
  enable_private_endpoint = false
  release_channel         = "STABLE"

}

module "project_iam_members" {
  source  = "terraform-google-modules/iam/google//modules/projects_iam"
  version = "~> 6.3.0"

  projects = [module.project.project_id]
  mode     = "additive"

  bindings = {
    "roles/logging.logWriter" = [
      "serviceAccount:${google_service_account.auth_server_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.hydra_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.response_datastore_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.study_builder_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.study_datastore_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.consent_datastore_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.enroll_datastore_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.user_datastore_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.participant_manager_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
      "serviceAccount:${google_service_account.triggers_pubsub_handler_gke_sa.account_id}@kyoto-univ-demo-apps.iam.gserviceaccount.com",
    ],
  }
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
