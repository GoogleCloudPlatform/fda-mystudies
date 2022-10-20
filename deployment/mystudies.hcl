# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# This is the solution template for MyStudies. Deployment specific
# values are to be filled in ./deployment.hcl.

# {{$recipes := "git://github.com/GoogleCloudPlatform/healthcare-data-protection-suite//templates/tfengine/recipes"}}
# {{$ref := "ref=templates-v0.4.0"}}

data = {
  parent_type     = "folder"
  parent_id       = "{{.folder_id}}"
  billing_account = "{{.billing_account}}"
  state_bucket    = "{{.prefix}}-{{.env}}-terraform-state"

  # Default locations for resources. Can be overridden in individual templates.
  bigquery_location   = "us-east1" # BigQuery is not available in "us-central1"
  cloud_sql_region    = "{{.default_location}}"
  cloud_sql_zone      = "{{.default_zone}}"
  compute_region      = "{{.default_location}}"
  compute_zone        = "{{.default_zone}}"
  gke_region          = "{{.default_location}}"
  healthcare_location = "{{.default_location}}"
  storage_location    = "{{.default_location}}"
  secret_locations    = ["{{.default_location}}"]
}

# Central devops project for Terraform state management and CI/CD.
template "devops" {
  recipe_path = "{{$recipes}}/devops.hcl?{{$ref}}"
  output_path = "./devops"
  data = {
    # During Step 1, set to `true` and re-run the engine after generated devops module has been deployed.
    # Run `terraform init` in the devops module to backup its state to GCS.
    enable_gcs_backend = false

    admins_group = "{{.prefix}}-{{.env}}-folder-admins@{{.domain}}"

    project = {
      project_id = "{{.prefix}}-{{.env}}-devops"
      owners = [
        "group:{{.prefix}}-{{.env}}-devops-owners@{{.domain}}",
      ]
      apis = [
        "container.googleapis.com",
        "firebase.googleapis.com",
        "iap.googleapis.com",
        "secretmanager.googleapis.com",
      ]
    }
  }
}

template "cicd" {
  recipe_path = "{{$recipes}}/cicd.hcl?{{$ref}}"
  output_path = "./cicd"
  data = {
    project_id = "{{.prefix}}-{{.env}}-devops"
    github = {
      owner = "{{.github_owner}}"
      name  = "{{.github_repo}}"
    }
    branch_name    = "{{.github_branch}}"
    terraform_root = "deployment/terraform"

    # Prepare and enable default triggers.
    triggers = {
      validate = {}
      plan     = {}
      apply    = {}
    }

    # IAM members to give the roles/cloudbuild.builds.viewer permission so they can see build results.
    build_viewers = [
      "group:{{.prefix}}-{{.env}}-cicd-viewers@{{.domain}}",
    ]

    managed_dirs = [
      "devops", // NOTE: CICD service account can only update APIs on the devops project.
      "audit",
      "{{.prefix}}-{{.env}}-secrets",
      "{{.prefix}}-{{.env}}-networks",
      "{{.prefix}}-{{.env}}-apps",
      "{{.prefix}}-{{.env}}-firebase",
      "{{.prefix}}-{{.env}}-data",
    ]
  }
}

template "audit" {
  recipe_path = "{{$recipes}}/audit.hcl?{{$ref}}"
  output_path = "./audit"
  data = {
    auditors_group = "{{.prefix}}-{{.env}}-auditors@{{.domain}}"
    project = {
      project_id = "{{.prefix}}-{{.env}}-audit"
    }
    logs_bigquery_dataset = {
      dataset_id = "{{.prefix}}_{{.env}}_1yr_audit_logs"
    }
    logs_storage_bucket = {
      name = "{{.prefix}}-{{.env}}-7yr-audit-logs"
    }
    additional_filters = [
      "logName=\\\"logs/application-audit-log\\\"",
    ]
  }
}

# Central secrets project and resources.
# NOTE: Any secret in this deployment that is not automatically filled in with
# a value must be filled manually in the GCP console secret manager page before
# any deployment can access its value.
template "project_secrets" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{.prefix}}-{{.env}}-secrets"
  data = {
    project = {
      project_id = "{{.prefix}}-{{.env}}-secrets"
      apis = [
        "secretmanager.googleapis.com"
      ]
    }
    resources = {
      secrets = [
        {
          secret_id = "manual-mystudies-email-address"
        },
        {
          secret_id = "manual-mystudies-email-password"
        },
        {
          secret_id = "manual-mystudies-from-email-address"
        },
        {
          secret_id = "manual-mystudies-contact-email-address"
        },
        {
          secret_id = "manual-mystudies-from-email-domain"
        },
        {
          secret_id = "manual-mystudies-smtp-hostname"
        },
        {
          secret_id = "manual-mystudies-smtp-use-ip-allowlist"
        },
        {
          secret_id = "manual-log-path"
        },
        {
          secret_id = "manual-org-name"
        },
        {
          secret_id = "manual-terms-url"
        },
        {
          secret_id = "manual-privacy-url"
        },
        {
          secret_id = "manual-fcm-api-url"
        },
        {
          secret_id = "manual-ios-deeplink-url"
        },
        {
          secret_id = "manual-android-deeplink-url"
        },
        {
          secret_id = "manual-region-id"
        },
        {
          secret_id = "manual-consent-enabled"
        },
        {
          secret_id = "manual-fhir-enabled"
        },
        {
          secret_id = "manual-discard-fhir"
        },
        {
          secret_id = "manual-ingest-data-to-bigquery"
        },		
        {
          secret_id   = "auto-mystudies-sql-default-user-password"
          secret_data = "$${random_password.passwords[\"mystudies_sql_default_user_password\"].result}"
        },
        {
          secret_id   = "auto-hydra-db-password"
          secret_data = "$${random_password.passwords[\"hydra_db_password\"].result}"
        },
        {
          secret_id   = "auto-hydra-db-user"
          secret_data = "$${random_string.strings[\"hydra_db_user\"].result}"
        },
        {
          secret_id   = "auto-hydra-system-secret"
          secret_data = "$${random_password.system_secrets[\"hydra_system_secret\"].result}"
        },
        {
          secret_id   = "auto-auth-server-db-user"
          secret_data = "$${random_string.strings[\"auth_server_db_user\"].result}"
        },
        {
          secret_id   = "auto-auth-server-db-password"
          secret_data = "$${random_password.passwords[\"auth_server_db_password\"].result}"
        },
        {
          secret_id   = "auto-auth-server-client-id"
          secret_data = "$${random_string.strings[\"auth_server_client_id\"].result}"
        },
        {
          secret_id   = "auto-auth-server-secret-key"
          secret_data = "$${random_password.passwords[\"auth_server_secret_key\"].result}"
        },
        {
          secret_id   = "auto-auth-server-encryptor-password"
          secret_data = "$${random_password.passwords[\"auth_server_encryptor_password\"].result}"
        },
        {
          secret_id   = "auto-response-datastore-db-user"
          secret_data = "$${random_string.strings[\"response_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-response-datastore-db-password"
          secret_data = "$${random_password.passwords[\"response_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-response-datastore-client-id"
          secret_data = "$${random_string.strings[\"response_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-response-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"response_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-study-builder-db-user"
          secret_data = "$${random_string.strings[\"study_builder_db_user\"].result}"
        },
        {
          secret_id   = "auto-study-builder-db-password"
          secret_data = "$${random_password.passwords[\"study_builder_db_password\"].result}"
        },
        {
          secret_id   = "auto-study-builder-client-id"
          secret_data = "$${random_string.strings[\"study_builder_client_id\"].result}"
        },
        {
          secret_id   = "auto-study-builder-secret-key"
          secret_data = "$${random_password.passwords[\"study_builder_secret_key\"].result}"
        },
        {
          secret_id   = "auto-study-datastore-db-user"
          secret_data = "$${random_string.strings[\"study_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-study-datastore-db-password"
          secret_data = "$${random_password.passwords[\"study_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-study-datastore-client-id"
          secret_data = "$${random_string.strings[\"study_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-study-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"study_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-participant-consent-datastore-db-user"
          secret_data = "$${random_string.strings[\"participant_consent_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-participant-consent-datastore-db-password"
          secret_data = "$${random_password.passwords[\"participant_consent_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-participant-consent-datastore-client-id"
          secret_data = "$${random_string.strings[\"participant_consent_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-participant-consent-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"participant_consent_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-participant-enroll-datastore-db-user"
          secret_data = "$${random_string.strings[\"participant_enroll_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-participant-enroll-datastore-db-password"
          secret_data = "$${random_password.passwords[\"participant_enroll_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-participant-enroll-datastore-client-id"
          secret_data = "$${random_string.strings[\"participant_enroll_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-participant-enroll-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"participant_enroll_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-participant-user-datastore-db-user"
          secret_data = "$${random_string.strings[\"participant_user_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-participant-user-datastore-db-password"
          secret_data = "$${random_password.passwords[\"participant_user_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-participant-user-datastore-client-id"
          secret_data = "$${random_string.strings[\"participant_user_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-participant-user-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"participant_user_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-datastore-db-user"
          secret_data = "$${random_string.strings[\"participant_manager_datastore_db_user\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-datastore-db-password"
          secret_data = "$${random_password.passwords[\"participant_manager_datastore_db_password\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-datastore-client-id"
          secret_data = "$${random_string.strings[\"participant_manager_datastore_client_id\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-datastore-secret-key"
          secret_data = "$${random_password.passwords[\"participant_manager_datastore_secret_key\"].result}"
        },
        {
          secret_id   = "auto-sd-response-datastore-token"
          secret_data = "$${random_password.tokens[\"sd_response_datastore_token\"].result}"
        },
        {
          secret_id   = "auto-sd-response-datastore-id"
          secret_data = "$${random_string.strings[\"sd_response_datastore_id\"].result}"
        },
        {
          secret_id   = "auto-sd-android-token"
          secret_data = "$${random_password.tokens[\"sd_android_token\"].result}"
        },
        {
          secret_id   = "auto-sd-android-id"
          secret_data = "$${random_string.strings[\"sd_android_id\"].result}"
        },
        {
          secret_id   = "auto-sd-ios-token"
          secret_data = "$${random_password.tokens[\"sd_ios_token\"].result}"
        },
        {
          secret_id   = "auto-sd-ios-id"
          secret_data = "$${random_string.strings[\"sd_ios_id\"].result}"
        },
      ]
    }
    terraform_addons = {
      raw_config = <<EOF
locals {
  apps = [
    "auth_server",
    "response_datastore",
    "study_builder",
    "study_datastore",
    "participant_consent_datastore",
    "participant_enroll_datastore",
    "participant_user_datastore",
    "participant_manager_datastore",
  ]
}

resource "random_string" "strings" {
  for_each = toset(concat(
    [
      "hydra_db_user",
      "sd_response_datastore_id",
      "sd_android_id",
      "sd_ios_id",
    ],
    formatlist("%s_db_user", local.apps),
    formatlist("%s_client_id", local.apps))
  )
  length  = 16
  special = false
}

resource "random_password" "passwords" {
  for_each = toset(concat(
    [
      "mystudies_sql_default_user_password",
      "hydra_db_password",
      "auth_server_encryptor_password"
    ],
    formatlist("%s_db_password", local.apps),
    formatlist("%s_secret_key", local.apps))
  )
  length  = 16
  special = false
}

resource "random_password" "tokens" {
  for_each = toset([
    "sd_response_datastore_token",
    "sd_android_token",
    "sd_ios_token",
  ])
  length  = 16
  special = false
}

resource "random_password" "system_secrets" {
  for_each = toset([
    "hydra_system_secret",
  ])
  length  = 32
  special = false
}
EOF
    }
  }
}

# Central networks host project and resources.
template "project_networks" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{.prefix}}-{{.env}}-networks"
  data = {
    project = {
      project_id         = "{{.prefix}}-{{.env}}-networks"
      is_shared_vpc_host = true
      apis = [
        "compute.googleapis.com",
        "container.googleapis.com",
        "iap.googleapis.com",
        "servicenetworking.googleapis.com",
        "sqladmin.googleapis.com",
      ]
    }
    resources = {
      compute_networks = [{
        name = "{{.prefix}}-{{.env}}-network"
        subnets = [
          {
            name = "{{.prefix}}-{{.env}}-bastion-subnet"
            # 10.0.128.0 --> 10.0.128.255
            ip_range = "10.0.128.0/24"
          },
          {
            name = "{{.prefix}}-{{.env}}-gke-subnet"
            # 10.0.0.0 --> 10.0.127.255
            ip_range = "10.0.0.0/17"
            secondary_ranges = [
              {
                name     = "{{.prefix}}-{{.env}}-pods-range"
                ip_range = "172.16.0.0/14"
              },
              {
                name     = "{{.prefix}}-{{.env}}-services-range"
                ip_range = "172.20.0.0/14"
              }
            ]
          },
        ]
        cloud_sql_private_service_access = {} # Enable SQL private service access.
      }]
      # To connect to the CloudSQL instance via the bastion VM:
      # $ gcloud compute ssh bastion-vm --zone={{.default_location}}-{{.default_zone}} --project={{.prefix}}-{{.env}}-networks
      # $ cloud_sql_proxy -instances={{.prefix}}-{{.env}}-data:{{.default_location}}:mystudies=tcp:3306
      # $ mysql -u default -p --host 127.0.0.1
      bastion_hosts = [{
        name          = "bastion-vm"
        network       = "$${module.{{.prefix}}_{{.env}}_network.network.network.self_link}"
        subnet        = "$${module.{{.prefix}}_{{.env}}_network.subnets[\"{{.default_location}}/{{.prefix}}-{{.env}}-bastion-subnet\"].self_link}"
        image_family  = "ubuntu-2004-lts"
        image_project = "ubuntu-os-cloud"
        members = [
          "group:{{.prefix}}-{{.env}}-bastion-accessors@{{.domain}}",
        ]
        startup_script = <<EOF
sudo apt-get -y update
sudo apt-get -y install mysql-client
sudo wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O /usr/local/bin/cloud_sql_proxy
sudo chmod +x /usr/local/bin/cloud_sql_proxy
EOF
      }]
      compute_routers = [{
        name    = "{{.prefix}}-{{.env}}-router"
        network = "$${module.{{.prefix}}_{{.env}}_network.network.network.self_link}"
        nats = [{
          name                               = "{{.prefix}}-{{.env}}-nat"
          source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"
          subnetworks = [
            {
              name                     = "$${module.{{.prefix}}_{{.env}}_network.subnets[\"{{.default_location}}/{{.prefix}}-{{.env}}-bastion-subnet\"].self_link}"
              source_ip_ranges_to_nat  = ["PRIMARY_IP_RANGE"]
              secondary_ip_range_names = []
            },
            {
              name                     = "$${module.{{.prefix}}_{{.env}}_network.subnets[\"{{.default_location}}/{{.prefix}}-{{.env}}-gke-subnet\"].self_link}"
              source_ip_ranges_to_nat  = ["ALL_IP_RANGES"]
              secondary_ip_range_names = []
            },
          ]
        }]
      }]
    }
    terraform_addons = {
      raw_config = <<EOF
resource "google_compute_firewall" "fw_allow_k8s_ingress_lb_health_checks" {
  name        = "fw-allow-k8s-ingress-lb-health-checks"
  description = "GCE L7 firewall rule"
  network     = module.{{.prefix}}_{{.env}}_network.network.network.self_link
  project     = module.project.project_id

  allow {
    protocol = "tcp"
    ports    = ["30000-32767"]
  }
  allow {
    protocol = "tcp"
    ports    = ["4444"]
  }
  allow {
    protocol = "tcp"
    ports    = ["80"]
  }
  allow {
    protocol = "tcp"
    ports    = ["8080"]
  }
  allow {
    protocol = "tcp"
    ports    = ["10256"]
  }

  # Load Balancer Health Check IP ranges.
  source_ranges = [
    "130.211.0.0/22",
    "209.85.152.0/22",
    "209.85.204.0/22",
    "35.191.0.0/16",
  ]

  target_tags = [
    "gke-{{.prefix}}-{{.env}}-gke-cluster",
    "gke-{{.prefix}}-{{.env}}-gke-cluster-default-node-pool",
  ]
}
EOF
    }
  }
}

# Apps project and resources.
template "project_apps" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{.prefix}}-{{.env}}-apps"
  data = {
    project = {
      project_id = "{{.prefix}}-{{.env}}-apps"
      apis = [
        "binaryauthorization.googleapis.com",
        "compute.googleapis.com",
        "container.googleapis.com",
        "dns.googleapis.com",
      ]
      shared_vpc_attachment = {
        host_project_id = "{{.prefix}}-{{.env}}-networks"
        subnets = [{
          name = "{{.prefix}}-{{.env}}-gke-subnet"
        }]
      }
    }
    resources = {
      gke_clusters = [{
        name                   = "{{.prefix}}-{{.env}}-gke-cluster"
        network_project_id     = "{{.prefix}}-{{.env}}-networks"
        network                = "{{.prefix}}-{{.env}}-network"
        subnet                 = "{{.prefix}}-{{.env}}-gke-subnet"
        ip_range_pods_name     = "{{.prefix}}-{{.env}}-pods-range"
        ip_range_services_name = "{{.prefix}}-{{.env}}-services-range"
        master_ipv4_cidr_block = "192.168.0.0/28"
        {{hclField . "master_authorized_networks"}}
      }]
      # Terraform-generated service account for use by the GKE apps.
      service_accounts = [
        { account_id = "auth-server-gke-sa" },
        { account_id = "hydra-gke-sa" },
        { account_id = "response-datastore-gke-sa" },
        { account_id = "study-builder-gke-sa" },
        { account_id = "study-datastore-gke-sa" },
        { account_id = "consent-datastore-gke-sa" },
        { account_id = "enroll-datastore-gke-sa" },
        { account_id = "user-datastore-gke-sa" },
        { account_id = "participant-manager-gke-sa" },
        { account_id = "triggers-pubsub-handler-gke-sa" },
      ]
      iam_members = {
        # Adding Logs Writer permission to service accounts for application level audit logs
        "roles/logging.logWriter" = [
          "serviceAccount:$${google_service_account.auth_server_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.hydra_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.response_datastore_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.study_builder_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.study_datastore_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.consent_datastore_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.enroll_datastore_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.user_datastore_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.participant_manager_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:$${google_service_account.triggers_pubsub_handler_gke_sa.account_id}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
      # BigQuery Permissions
        "roles/bigquery.admin" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",		  
        ]
      }
      # Binary Authorization resources.
      # Simple configuration for now. Future
      # See https://cloud.google.com/binary-authorization/docs/overview
      binary_authorization = {
        admission_whitelist_patterns = [{
          name_pattern = "gcr.io/cloudsql-docker/*"
          },
          {
          name_pattern = "gcr.io/gke-release/istio/*" 
          },
          {
          name_pattern = "docker.io/prom/*"
            }
          ]
      }
      # DNS sets up nameservers to connect to the GKE clusters.
      dns_zones = [{
        name   = "{{.prefix}}-{{.env}}"
        domain = "{{.prefix}}-{{.env}}.{{.domain}}."
        type   = "public"
        record_sets = [{
          name = "participants"
          type = "A"
          ttl  = 30
          records = [
            "$${google_compute_global_address.ingress_static_ip.address}",
          ]
        },
        {
          name = "studies"
          type = "A"
          ttl  = 30
          records = [
            "$${google_compute_global_address.ingress_static_ip.address}",
          ]
        }]
      }]
    }
    terraform_addons = {
      raw_config = <<EOF
# Reserve a static external IP for the Ingress.
resource "google_compute_global_address" "ingress_static_ip" {
  name         = "{{.prefix}}-{{.env}}-ingress-ip"
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
#7#     owner = "{{.github_owner}}"
#7#     name  = "{{.github_repo}}"
#7#     push { branch = "^{{.github_branch}}$" }
#7#   }

#7#   filename = "$${each.key}/cloudbuild.yaml"
#7# }
EOF
    }
  }
}

# Firebase project and resources.
template "project_firebase" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{.prefix}}-{{.env}}-firebase"
  data = {
    project = {
      project_id = "{{.prefix}}-{{.env}}-firebase"
      apis = [
        "firebase.googleapis.com",
      ]
    }
  }
}

# Data project and resources.
template "project_data" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{.prefix}}-{{.env}}-data"
  data = {
    project = {
      project_id = "{{.prefix}}-{{.env}}-data"
      apis = [
        "bigquery.googleapis.com",
        "compute.googleapis.com",
        "servicenetworking.googleapis.com",
        "sqladmin.googleapis.com",
        "healthcare.googleapis.com",		
      ]
      shared_vpc_attachment = {
        host_project_id = "{{.prefix}}-{{.env}}-networks"
      }
    }
    # Step 5.1: uncomment and re-run the engine once all previous steps have been completed.
#5#     terraform_addons = {
#5#       raw_config = <<EOF
#5# locals {
#5#   apps = [
#5#     "auth-server",
#5#     "response-datastore",
#5#     "study-builder",
#5#     "study-datastore",
#5#     "participant-consent-datastore",
#5#     "participant-enroll-datastore",
#5#     "participant-user-datastore",
#5#     "participant-manager-datastore",
#5#     "hydra",
#5#   ]
#5# }

#5# data "google_secret_manager_secret_version" "db_secrets" {
#5#   provider = google-beta
#5#   project  = "{{.prefix}}-{{.env}}-secrets"
#5#   secret   = each.key

#5#   for_each = toset(concat(
#5#     ["auto-mystudies-sql-default-user-password"],
#5#     formatlist("auto-%s-db-user", local.apps),
#5#     formatlist("auto-%s-db-password", local.apps))
#5#   )
#5# }

#5# resource "google_sql_user" "db_users" {
#5#   for_each = toset(local.apps)

#5#   name     = data.google_secret_manager_secret_version.db_secrets["auto-$${each.key}-db-user"].secret_data
#5#   instance = module.mystudies.instance_name
#5#   host     = "%"
#5#   password = data.google_secret_manager_secret_version.db_secrets["auto-$${each.key}-db-password"].secret_data
#5#   project  = module.project.project_id
#5# }
#5# EOF
#5#     }
    resources = {
      # Step 5.2: uncomment and re-run the engine once all previous steps have been completed.
      #5# cloud_sql_instances = [{
      #5#   name               = "mystudies"
      #5#   type               = "mysql"
      #5#   network_project_id = "{{.prefix}}-{{.env}}-networks"
      #5#   network            = "{{.prefix}}-{{.env}}-network"
      #5#   user_password      = "$${data.google_secret_manager_secret_version.db_secrets[\"auto-mystudies-sql-default-user-password\"].secret_data}"
      #5# }]
      iam_members = {
        "roles/cloudsql.client" = [
          "serviceAccount:bastion@{{.prefix}}-{{.env}}-networks.iam.gserviceaccount.com",
          "serviceAccount:auth-server-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:hydra-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:consent-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:enroll-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:user-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:participant-manager-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:triggers-pubsub-handler-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
	  # HEALTH CARE API Roles/Permissions
        "roles/healthcare.consentArtifactEditor" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:consent-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:enroll-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:user-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/healthcare.consentEditor" = [
          "serviceAccount:consent-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:enroll-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:user-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/healthcare.consentArtifactReader" = [
          "serviceAccount:participant-manager-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",  
        ]
        "roles/healthcare.consentReader" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",  
        ]
        "roles/healthcare.consentStoreViewer" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",  
        ]
        "roles/healthcare.datasetAdmin" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/healthcare.consentStoreAdmin" = [
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/healthcare.fhirStoreAdmin" = [
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/healthcare.fhirResourceEditor" = [
          "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]	
	    # BigQuery Permissions
        #6# "roles/bigquery.admin" = [
        #6#  "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        #6#  "serviceAccount:user-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        #6#  "serviceAccount:service-$${module.project.project_number}@gcp-sa-healthcare.iam.gserviceaccount.com",  
        #6# ]
        "roles/bigquery.dataEditor" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/datastore.user" = [
          "serviceAccount:response-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:user-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com",
        ]
        #6# "roles/storage.objectAdmin" = [
        #6#  "serviceAccount:service-$${module.project.project_number}@gcp-sa-healthcare.iam.gserviceaccount.com",
        #6# ]
      }
      storage_buckets = [
        {
          name = "{{.prefix}}-{{.env}}-mystudies-consent-documents"
          #6# iam_members = [
          #6# {
          #6#   role   = "roles/storage.objectAdmin"
          #6# member = "serviceAccount:consent-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          #6# },
          #6# {
          #6# role   = "roles/storage.objectAdmin"
          #6# member = "serviceAccount:participant-manager-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          #6# },
          #6# {
          #6# role   = "roles/storage.objectAdmin"
          #6# member = "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          #6# },			
	      # HEALTHCARE API SA role bindling to consent bucket
          #6# {
          #6# role   = "roles/storage.objectViewer"
          #6# member = "serviceAccount:service-$${module.project.project_number}@gcp-sa-healthcare.iam.gserviceaccount.com"
          #6# },			
          #6# ]
        },
        {
          name = "{{.prefix}}-{{.env}}-mystudies-study-resources"
          iam_members = [{
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          },
          {
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:study-datastore-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          },
          {
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:participant-manager-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          }]
        },
        {
          name = "{{.prefix}}-{{.env}}-mystudies-sql-import"
          # Step 6: uncomment and re-run the engine once all previous steps have been completed.
          #6# iam_members = [{
          #6#   role   = "roles/storage.objectViewer"
          #6#   member = "serviceAccount:$${module.mystudies.instance_service_account_email_address}"
          #6# },
          #6# {
          #6#   role   = "roles/storage.objectAdmin"
          #6#   member = "serviceAccount:study-builder-gke-sa@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
          #6# }]
        },
      ]
    }
  }
}

# Kubernetes Terraform deployment. This should be deployed manually as Cloud
# Build cannot access the GKE cluster and should be deployed after the GKE
# Cluster has been deployed.
template "kubernetes" {
  recipe_path = "{{$recipes}}/deployment.hcl?{{$ref}}"
  output_path = "./kubernetes"
  data = {
    state_path_prefix = "kubernetes"
    terraform_addons = {
      raw_config = <<EOF
data "google_client_config" "default" {}

data "google_container_cluster" "gke_cluster" {
  name     = "{{.prefix}}-{{.env}}-gke-cluster"
  location = "{{.default_location}}"
  project  = "{{.prefix}}-{{.env}}-apps"
}

provider "kubernetes" {
  version                = "1.13.3"
  load_config_file       = false
  token                  = data.google_client_config.default.access_token
  host                   = data.google_container_cluster.gke_cluster.endpoint
  client_certificate     = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_certificate)
  client_key             = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_key)
  cluster_ca_certificate = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.cluster_ca_certificate)
}

locals {
  # hydra is treated separately.
  apps = [
    "auth-server",
    "response-datastore",
    "study-builder",
    "study-datastore",
    "participant-consent-datastore",
    "participant-enroll-datastore",
    "participant-user-datastore",
    "participant-manager-datastore",
  ]
  apps_db_names = {
    "auth-server"                   = "oauth_server_hydra"
    "response-datastore"            = "mystudies_response_server"
    "study-builder"                 = "fda_hphc"
    "study-datastore"               = "fda_hphc"
    "participant-consent-datastore" = "mystudies_participant_datastore"
    "participant-enroll-datastore"  = "mystudies_participant_datastore"
    "participant-user-datastore"    = "mystudies_participant_datastore"
    "participant-manager-datastore" = "mystudies_participant_datastore"
  }
  service_account_ids = [
    "auth-server-gke-sa",
    "hydra-gke-sa",
    "response-datastore-gke-sa",
    "study-builder-gke-sa",
    "study-datastore-gke-sa",
    "consent-datastore-gke-sa",
    "enroll-datastore-gke-sa",
    "user-datastore-gke-sa",
    "participant-manager-gke-sa",
  ]
}

# Data sources from Secret Manager.
data "google_secret_manager_secret_version" "secrets" {
  provider = google-beta
  project  = "{{.prefix}}-{{.env}}-secrets"
  secret   = each.key

  for_each = toset(concat(
    [
      "manual-mystudies-email-address",
      "manual-mystudies-email-password",
      "manual-mystudies-contact-email-address",
      "manual-mystudies-from-email-address",
      "manual-mystudies-from-email-domain",
      "manual-mystudies-smtp-hostname",
      "manual-mystudies-smtp-use-ip-allowlist",
      "manual-log-path",
      "manual-org-name",
      "manual-terms-url",
      "manual-privacy-url",
      "manual-fcm-api-url",
      "manual-ios-deeplink-url",
      "manual-android-deeplink-url",
      "manual-region-id",
      "manual-consent-enabled",
      "manual-fhir-enabled",
      "manual-discard-fhir",
      "manual-ingest-data-to-bigquery",	  
      "auto-auth-server-encryptor-password",
      "auto-hydra-db-password",
      "auto-hydra-db-user",
      "auto-hydra-system-secret",
      "auto-sd-response-datastore-token",
      "auto-sd-response-datastore-id",
      "auto-sd-android-token",
      "auto-sd-android-id",
      "auto-sd-ios-token",
      "auto-sd-ios-id",
    ],
    formatlist("auto-%s-db-user", local.apps),
    formatlist("auto-%s-db-password", local.apps),
    formatlist("auto-%s-client-id", local.apps),
    formatlist("auto-%s-secret-key", local.apps))
  )
}

# Shared secrets.
resource "kubernetes_secret" "shared_secrets" {
  metadata {
    name = "shared-secrets"
  }

  data = {
    consent_bucket_name               = "{{.prefix}}-{{.env}}-mystudies-consent-documents"
    study_resources_bucket_name       = "{{.prefix}}-{{.env}}-mystudies-study-resources"
    study_export_import_bucket_name   = "{{.prefix}}-{{.env}}-mystudies-sql-import"
    institution_resources_bucket_name = "{{.prefix}}-{{.env}}-mystudies-institution-resources"
    base_url                          = "https://participants.{{.prefix}}-{{.env}}.{{.domain}}"
    studies_base_url                  = "https://studies.{{.prefix}}-{{.env}}.{{.domain}}"
    data_project_id                   = "{{.prefix}}-{{.env}}-data"
    firestore_project_id              = "{{.prefix}}-{{.env}}-firebase"	
    log_path                          = data.google_secret_manager_secret_version.secrets["manual-log-path"].secret_data
    org_name                          = data.google_secret_manager_secret_version.secrets["manual-org-name"].secret_data
    terms_url                         = data.google_secret_manager_secret_version.secrets["manual-terms-url"].secret_data
    privacy_url                       = data.google_secret_manager_secret_version.secrets["manual-privacy-url"].secret_data
    fcm_api_url                       = data.google_secret_manager_secret_version.secrets["manual-fcm-api-url"].secret_data
    region_id                         = data.google_secret_manager_secret_version.secrets["manual-region-id"].secret_data
    consent_enabled                   = data.google_secret_manager_secret_version.secrets["manual-consent-enabled"].secret_data
    fhir_enabled                      = data.google_secret_manager_secret_version.secrets["manual-fhir-enabled"].secret_data
    discard_fhir                      = data.google_secret_manager_secret_version.secrets["manual-discard-fhir"].secret_data
    ingest_data_to_bigquery           = data.google_secret_manager_secret_version.secrets["manual-ingest-data-to-bigquery"].secret_data	
  }
}

# App credentials.
resource "kubernetes_secret" "apps_credentials" {
  for_each = toset(local.apps)

  metadata {
    name = "$${each.key}-credentials"
  }

  data = {
    dbusername  = data.google_secret_manager_secret_version.secrets["auto-$${each.key}-db-user"].secret_data
    dbpassword  = data.google_secret_manager_secret_version.secrets["auto-$${each.key}-db-password"].secret_data
    client_id   = data.google_secret_manager_secret_version.secrets["auto-$${each.key}-client-id"].secret_data
    secret_key  = data.google_secret_manager_secret_version.secrets["auto-$${each.key}-secret-key"].secret_data
    dbname      = local.apps_db_names[each.key]
  }
}

# Client-side credentials.
resource "kubernetes_secret" "client_side_credentials" {

  metadata {
    name = "client-side-credentials"
  }

  data = {
    client_id  = data.google_secret_manager_secret_version.secrets["auto-auth-server-client-id"].secret_data
    secret_key = data.google_secret_manager_secret_version.secrets["auto-auth-server-secret-key"].secret_data
  }
}

# Auth-server secrets.
resource "kubernetes_secret" "auth_server_secrets" {

  metadata {
    name = "auth-server-secrets"
  }

  data = {
    encryptor_password   = data.google_secret_manager_secret_version.secrets["auto-auth-server-encryptor-password"].secret_data
    ios_deeplink_url     = data.google_secret_manager_secret_version.secrets["manual-ios-deeplink-url"].secret_data
    android_deeplink_url = data.google_secret_manager_secret_version.secrets["manual-android-deeplink-url"].secret_data
  }
}


# Hydra credentials.
resource "kubernetes_secret" "hydra_credentials" {

  metadata {
    name = "hydra-credentials"
  }

  data = {
    dbusername    = data.google_secret_manager_secret_version.secrets["auto-hydra-db-user"].secret_data
    dbpassword    = data.google_secret_manager_secret_version.secrets["auto-hydra-db-password"].secret_data
    system_secret = data.google_secret_manager_secret_version.secrets["auto-hydra-system-secret"].secret_data
    dbname        = "hydra"
  }
}

# Study datastore connect credentials.
resource "kubernetes_secret" "study_datastore_connect_credentials" {
  metadata {
    name = "study-datastore-connect-credentials"
  }
  data = {
    response_datastore_id      = data.google_secret_manager_secret_version.secrets["auto-sd-response-datastore-id"].secret_data
    response_datastore_token   = data.google_secret_manager_secret_version.secrets["auto-sd-response-datastore-token"].secret_data
    android_id                 = data.google_secret_manager_secret_version.secrets["auto-sd-android-id"].secret_data
    android_token              = data.google_secret_manager_secret_version.secrets["auto-sd-android-token"].secret_data
    ios_id                     = data.google_secret_manager_secret_version.secrets["auto-sd-ios-id"].secret_data
    ios_token                  = data.google_secret_manager_secret_version.secrets["auto-sd-ios-token"].secret_data
  }
}

# Email credentials.
resource "kubernetes_secret" "email_credentials" {
  metadata {
    name = "email-credentials"
  }

  data = {
    email_address         = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-address"].secret_data
    email_password        = data.google_secret_manager_secret_version.secrets["manual-mystudies-email-password"].secret_data
    contact_email_address = data.google_secret_manager_secret_version.secrets["manual-mystudies-contact-email-address"].secret_data
    from_email_address    = data.google_secret_manager_secret_version.secrets["manual-mystudies-from-email-address"].secret_data
    from_email_domain     = data.google_secret_manager_secret_version.secrets["manual-mystudies-from-email-domain"].secret_data
    smtp_hostname         = data.google_secret_manager_secret_version.secrets["manual-mystudies-smtp-hostname"].secret_data
    smtp_use_ip_allowlist = data.google_secret_manager_secret_version.secrets["manual-mystudies-smtp-use-ip-allowlist"].secret_data
  }
}

# gcloud keys from service accounts
resource "google_service_account_key" "apps_service_account_keys" {
  for_each = toset(local.service_account_ids)

  service_account_id = "$${each.key}@{{.prefix}}-{{.env}}-apps.iam.gserviceaccount.com"
}

resource "kubernetes_secret" "apps_gcloud_keys" {
  for_each = toset(local.service_account_ids)

  metadata {
    name = "$${each.key}-gcloud-key"
  }
  data = {
    "key.json" = base64decode(google_service_account_key.apps_service_account_keys[each.key].private_key)
  }
}
EOF
    }
  }
}
