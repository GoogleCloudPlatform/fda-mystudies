# {{$recipes := "git://github.com/GoogleCloudPlatform/healthcare-data-protection-suite//templates/tfengine/recipes"}}
# {{$ref := "ref=templates-v0.2.0"}}

# {{$prefix := "example"}}
# {{$env := "dev"}}
# {{$domain := "example.com"}}
# {{$default_location := "us-central1"}}
# {{$default_zone := "a"}}

data = {
  parent_type     = "folder"
  parent_id       = "0000000000"
  billing_account = "XXXXXX-XXXXXX-XXXXXX"
  state_bucket    = "{{$prefix}}-{{$env}}-terraform-state"

  # Default locations for resources. Can be overridden in individual templates.
  bigquery_location   = "us-east1" # BigQuery is not available in "us-central1"
  cloud_sql_region    = "{{$default_location}}"
  cloud_sql_zone      = "{{$default_zone}}"
  compute_region      = "{{$default_location}}"
  compute_zone        = "{{$default_zone}}"
  gke_region          = "{{$default_location}}"
  healthcare_location = "{{$default_location}}"
  storage_location    = "{{$default_location}}"
  secret_locations    = ["{{$default_location}}"]
}

# Central devops project for Terraform state management and CI/CD.
template "devops" {
  recipe_path = "{{$recipes}}/devops.hcl?{{$ref}}"
  output_path = "./devops"
  data = {
    # During Step 1, set to `true` and re-run the engine after generated devops module has been deployed.
    # Run `terraform init` in the devops module to backup its state to GCS.
    enable_gcs_backend = false

    admins_group = "{{$prefix}}-{{$env}}-folder-admins@{{$domain}}"

    project = {
      project_id = "{{$prefix}}-{{$env}}-devops"
      owners = [
        "group:{{$prefix}}-{{$env}}-devops-owners@{{$domain}}",
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
    project_id = "{{$prefix}}-{{$env}}-devops"
    github = {
      owner = "GoogleCloudPlatform"
      name  = "example"
    }
    branch_regex   = "^master$"
    terraform_root = "terraform"

    # Prepare and enable default triggers.
    triggers = {
      validate = {}
      plan     = {}
      apply    = {}
    }

    # IAM members to give the roles/cloudbuild.builds.viewer permission so they can see build results.
    build_viewers = [
      "group:{{$prefix}}-{{$env}}-cicd-viewers@{{$domain}}",
    ]

    managed_dirs = [
      "devops", // NOTE: CICD service account can only update APIs on the devops project.
      "audit",
      "{{$prefix}}-{{$env}}-secrets",
      "{{$prefix}}-{{$env}}-networks",
      "{{$prefix}}-{{$env}}-apps",
      "{{$prefix}}-{{$env}}-firebase",
      "{{$prefix}}-{{$env}}-data",
    ]
  }
}

template "audit" {
  recipe_path = "{{$recipes}}/audit.hcl?{{$ref}}"
  output_path = "./audit"
  data = {
    auditors_group = "{{$prefix}}-{{$env}}-auditors@{{$domain}}"
    project = {
      project_id = "{{$prefix}}-{{$env}}-audit"
    }
    logs_bigquery_dataset = {
      dataset_id = "{{$prefix}}_{{$env}}_1yr_audit_logs"
    }
    logs_storage_bucket = {
      name = "{{$prefix}}-{{$env}}-7yr-audit-logs"
    }
  }
}

# Central secrets project and resources.
# NOTE: Any secret in this deployment that is not automatically filled in with
# a value must be filled manually in the GCP console secret manager page before
# any deployment can access its value.
template "project_secrets" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{$prefix}}-{{$env}}-secrets"
  data = {
    project = {
      project_id = "{{$prefix}}-{{$env}}-secrets"
      apis = [
        "secretmanager.googleapis.com"
      ]
    }
    resources = {
      secrets = [
        {
          secret_id = "manual-mystudies-wcp-user"
        },
        {
          secret_id = "manual-mystudies-wcp-password"
        },
        {
          secret_id = "manual-mystudies-email-address"
        },
        {
          secret_id = "manual-mystudies-email-password"
        },
        # AppId for the mobile app. This needs to be in the app_info table in user registration database.
        {
          secret_id = "manual-mobile-app-appid"
        },
        # bundleID used for the Android App.
        {
          secret_id = "manual-android-bundle-id"
        },
        # Found under settings > cloud messaging in the android app defined in your firebase project.
        {
          secret_id = "manual-android-server-key"
        },
        # bundleID used to build and distribute the iOS App.
        {
          secret_id = "manual-ios-bundle-id"
        },
        # certificate and password generated for APNs.
        {
          secret_id = "manual-ios-certificate"
        },
        {
          secret_id = "manual-ios-certificate-password"
        },
        {
          secret_id   = "auto-auth-server-db-password"
          secret_data = "$${random_password.passwords[\"auth_server_db_password\"].result}"
        },
        {
          secret_id   = "auto-auth-server-db-user"
          secret_data = "$${random_string.strings[\"auth_server_db_user\"].result}"
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
          secret_id   = "auto-hydra-secrets-system"
          secret_data = "$${random_password.secrets[\"hydra_secrets_key\"].result}"
        },
        {
          secret_id   = "auto-mystudies-ma-client-id"
          secret_data = "$${random_string.strings[\"mystudies_ma_client_id\"].result}"
        },
        {
          secret_id   = "auto-mystudies-ma-secret-key"
          secret_data = "$${random_password.passwords[\"mystudies_ma_secret_key\"].result}"
        },
        {
          secret_id   = "auto-mystudies-rs-client-id"
          secret_data = "$${random_string.strings[\"mystudies_rs_client_id\"].result}"
        },
        {
          secret_id   = "auto-mystudies-rs-secret-key"
          secret_data = "$${random_password.passwords[\"mystudies_rs_secret_key\"].result}"
        },
        {
          secret_id   = "auto-mystudies-sql-default-user-password"
          secret_data = "$${random_password.passwords[\"mystudies_sql_default_user_password\"].result}"
        },
        {
          secret_id   = "auto-mystudies-urs-client-id"
          secret_data = "$${random_string.strings[\"mystudies_urs_client_id\"].result}"
        },
        {
          secret_id   = "auto-mystudies-urs-secret-key"
          secret_data = "$${random_password.passwords[\"mystudies_urs_secret_key\"].result}"
        },
        {
          secret_id   = "auto-mystudies-wcp-client-id"
          secret_data = "$${random_string.strings[\"mystudies_wcp_client_id\"].result}"
        },
        {
          secret_id   = "auto-mystudies-wcp-secret-key"
          secret_data = "$${random_password.passwords[\"mystudies_wcp_secret_key\"].result}"
        },
        {
          secret_id   = "auto-response-server-db-password"
          secret_data = "$${random_password.passwords[\"response_server_db_password\"].result}"
        },
        {
          secret_id   = "auto-response-server-db-user"
          secret_data = "$${random_string.strings[\"response_server_db_user\"].result}"
        },
        {
          secret_id   = "auto-study-designer-db-password"
          secret_data = "$${random_password.passwords[\"study_designer_db_password\"].result}"
        },
        {
          secret_id   = "auto-study-designer-db-user"
          secret_data = "$${random_string.strings[\"study_designer_db_user\"].result}"
        },
        {
          secret_id   = "auto-study-metadata-db-password"
          secret_data = "$${random_password.passwords[\"study_metadata_db_password\"].result}"
        },
        {
          secret_id   = "auto-study-metadata-db-user"
          secret_data = "$${random_string.strings[\"study_metadata_db_user\"].result}"
        },
        {
          secret_id   = "auto-user-registration-db-password"
          secret_data = "$${random_password.passwords[\"user_registration_db_password\"].result}"
        },
        {
          secret_id   = "auto-user-registration-db-user"
          secret_data = "$${random_string.strings[\"user_registration_db_user\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-db-password"
          secret_data = "$${random_password.passwords[\"participant_manager_db_password\"].result}"
        },
        {
          secret_id   = "auto-participant-manager-db-user"
          secret_data = "$${random_string.strings[\"participant_manager_db_user\"].result}"
        },
      ]
    }
    terraform_addons = {
      raw_config = <<EOF
resource "random_string" "strings" {
  for_each = toset([
    "auth_server_db_user",
    "mystudies_ma_client_id",
    "mystudies_rs_client_id",
    "mystudies_urs_client_id",
    "mystudies_wcp_client_id",
    "response_server_db_user",
    "study_designer_db_user",
    "study_metadata_db_user",
    "user_registration_db_user",
    "participant_manager_db_user",
    "hydra_db_user",
  ])
  length  = 16
  special = true
}

resource "random_password" "passwords" {
  for_each = toset([
    "auth_server_db_password",
    "mystudies_ma_secret_key",
    "mystudies_rs_secret_key",
    "mystudies_sql_default_user_password",
    "mystudies_urs_secret_key",
    "mystudies_wcp_secret_key",
    "response_server_db_password",
    "study_designer_db_password",
    "study_metadata_db_password",
    "user_registration_db_password",
    "participant_manager_db_user",
    "hydra_db_password",
  ])
  length  = 16
  special = true
}

resource "random_password" "secrets" {
  for_each = toset([
    "hydra_secrets_key",
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
  output_path = "./{{$prefix}}-{{$env}}-networks"
  data = {
    project = {
      project_id         = "{{$prefix}}-{{$env}}-networks"
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
        name = "{{$prefix}}-{{$env}}-network"
        subnets = [
          {
            name = "{{$prefix}}-{{$env}}-bastion-subnet"
            # 10.0.128.0 --> 10.0.128.255
            ip_range = "10.0.128.0/24"
          },
          {
            name = "{{$prefix}}-{{$env}}-gke-subnet"
            # 10.0.0.0 --> 10.0.127.255
            ip_range = "10.0.0.0/17"
            secondary_ranges = [
              {
                name     = "{{$prefix}}-{{$env}}-pods-range"
                ip_range = "172.16.0.0/14"
              },
              {
                name     = "{{$prefix}}-{{$env}}-services-range"
                ip_range = "172.20.0.0/14"
              }
            ]
          },
        ]
        cloud_sql_private_service_access = {} # Enable SQL private service access.
      }]
      # To connect to the CloudSQL instance via the bastion VM:
      # $ gcloud compute ssh bastion-vm --zone={{$default_location}}-{{$default_zone}} --project={{$prefix}}-{{$env}}-networks
      # $ cloud_sql_proxy -instances={{$prefix}}-{{$env}}-data:{{$default_location}}:mystudies=tcp:3306
      # $ mysql -u default -p --host 127.0.0.1
      bastion_hosts = [{
        name          = "bastion-vm"
        network       = "$${module.{{$prefix}}_{{$env}}_network.network.network.self_link}"
        subnet        = "$${module.{{$prefix}}_{{$env}}_network.subnets[\"{{$default_location}}/{{$prefix}}-{{$env}}-bastion-subnet\"].self_link}"
        image_family  = "ubuntu-2004-lts"
        image_project = "ubuntu-os-cloud"
        members = [
          "group:{{$prefix}}-{{$env}}-bastion-accessors@{{$domain}}",
        ]
        startup_script = <<EOF
sudo apt-get -y update
sudo apt-get -y install mysql-client
sudo wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O /usr/local/bin/cloud_sql_proxy
sudo chmod +x /usr/local/bin/cloud_sql_proxy
EOF
      }]
      compute_routers = [{
        name    = "{{$prefix}}-{{$env}}-router"
        network = "$${module.{{$prefix}}_{{$env}}_network.network.network.self_link}"
        nats = [{
          name                               = "{{$prefix}}-{{$env}}-nat"
          source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"
          subnetworks = [
            {
              name                     = "$${module.{{$prefix}}_{{$env}}_network.subnets[\"{{$default_location}}/{{$prefix}}-{{$env}}-bastion-subnet\"].self_link}"
              source_ip_ranges_to_nat  = ["PRIMARY_IP_RANGE"]
              secondary_ip_range_names = []
            },
            {
              name                     = "$${module.{{$prefix}}_{{$env}}_network.subnets[\"{{$default_location}}/{{$prefix}}-{{$env}}-gke-subnet\"].self_link}"
              source_ip_ranges_to_nat  = ["ALL_IP_RANGES"]
              secondary_ip_range_names = []
            },
          ]
        }]
      }]
    }
  }
}

# Apps project and resources.
template "project_apps" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{$prefix}}-{{$env}}-apps"
  data = {
    project = {
      project_id = "{{$prefix}}-{{$env}}-apps"
      apis = [
        "binaryauthorization.googleapis.com",
        "compute.googleapis.com",
        "container.googleapis.com",
        "dns.googleapis.com",
      ]
      shared_vpc_attachment = {
        host_project_id = "{{$prefix}}-{{$env}}-networks"
        subnets = [{
          name = "{{$prefix}}-{{$env}}-gke-subnet"
        }]
      }
    }
    resources = {
      gke_clusters = [{
        name                   = "{{$prefix}}-{{$env}}-gke-cluster"
        network_project_id     = "{{$prefix}}-{{$env}}-networks"
        network                = "{{$prefix}}-{{$env}}-network"
        subnet                 = "{{$prefix}}-{{$env}}-gke-subnet"
        ip_range_pods_name     = "{{$prefix}}-{{$env}}-pods-range"
        ip_range_services_name = "{{$prefix}}-{{$env}}-services-range"
        master_ipv4_cidr_block = "192.168.0.0/28"
        master_authorized_networks = [
          {
            cidr_block   = "104.132.0.0/14"
            display_name = "Google Offices/Campuses/CorpDC"
          }
        ]
      }]
      # Terraform-generated service account for use by the GKE apps.
      service_accounts = [
        { account_id = "auth-server-gke-sa" },
        { account_id = "hydra-gke-sa" },
        { account_id = "response-server-gke-sa" },
        { account_id = "study-designer-gke-sa" },
        { account_id = "study-metadata-gke-sa" },
        { account_id = "user-registration-gke-sa" },
        { account_id = "participant-manager-gke-sa" },
        { account_id = "triggers-pubsub-handler-gke-sa" },
      ]
      # Binary Authorization resources.
      # Simple configuration for now. Future
      # See https://cloud.google.com/binary-authorization/docs/overview
      binary_authorization = {
        admission_whitelist_patterns = [{
          name_pattern = "gcr.io/cloudsql-docker/*"
        }]
      }
      # DNS sets up nameservers to connect to the GKE clusters.
      dns_zones = [{
        name   = "{{$prefix}}-{{$env}}"
        domain = "{{$prefix}}-{{$env}}.{{$domain}}."
        type   = "public"
        record_sets = [{
          name = "{{$prefix}}-{{$env}}"
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

# resource "google_cloudbuild_trigger" "server_build_triggers" {
#   for_each = toset([
#     "WCP",
#     "WCP-WS",
#     "oauth-scim-module",
#     "user-registration-server-ws/consent-mgmt-module",
#     "user-registration-server-ws/enroll-mgmt-module",
#     "user-registration-server-ws/user-mgmt-module",
#     "response-server-ws",
#     "participant-manager-module",
#     "hydra",
#     "UR-web-app",
#   ])
#
#   provider = google-beta
#   project  = module.project.project_id
#   name     = each.key
#
#   included_files = ["$${each.key}/**"]
#
#   github {
#     owner = "GoogleCloudPlatform"
#     name  = "example"
#     push { branch = "^master$" }
#   }
#
#   filename = "$${each.key}/cloudbuild.yaml"
# }
EOF
    }
  }
}

# Firebase project and resources.
template "project_firebase" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{$prefix}}-{{$env}}-firebase"
  data = {
    project = {
      project_id = "{{$prefix}}-{{$env}}-firebase"
      apis = [
        "firebase.googleapis.com",
      ]
    }
    resources = {
      iam_members = {
        "roles/datastore.importExportAdmin" = [
          "serviceAccount:$${google_firebase_project.firebase.project}@appspot.gserviceaccount.com",
        ]
        "roles/datastore.user" = [
          "serviceAccount:response-server-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:triggers-pubsub-handler-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/pubsub.subscriber" = [
          "serviceAccount:triggers-pubsub-handler-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
        ]
      }
      storage_buckets = [
        {
          # Firestore data export
          name = "{{$prefix}}-{{$env}}-mystudies-firestore-raw-data"
          iam_members = [{
            role   = "roles/storage.admin"
            member = "serviceAccount:$${google_firebase_project.firebase.project}@appspot.gserviceaccount.com"
          }]
          # TTL 7 days.
          lifecycle_rules = [{
            action = {
              type = "Delete"
            }
            condition = {
              age        = 7 # 7 days
              with_state = "ANY"
            }
          }]
        },
      ]
      # Terraform-generated service account for use by Cloud Function.
      service_accounts = [
        { account_id = "raw-data-export" },
        { account_id = "bigquery-export" },
        { account_id = "real-time-triggers" },
      ]
      pubsub_topics = [{
        name = "surveyWriteTrigger"
        pull_subscriptions = [
          {
            name                 = "surveyWriteGlobal"
            ack_deadline_seconds = 10
          }
        ]
      }]
    }
    terraform_addons = {
      raw_config = <<EOF
resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = module.project.project_id
}

# Step 5.1: uncomment and re-run the engine once all previous steps have been completed.
# resource "google_firestore_index" "activities_index" {
#   project    = module.project.project_id
#   collection = "Activities"
#   fields {
#     field_path = "participantId"
#     order      = "ASCENDING"
#   }
#   fields {
#     field_path = "createdTimestamp"
#     order      = "ASCENDING"
#   }
#   fields {
#     field_path = "__name__"
#     order      = "ASCENDING"
#   }
# }
EOF
    }
  }
}

# Data project and resources.
template "project_data" {
  recipe_path = "{{$recipes}}/project.hcl?{{$ref}}"
  output_path = "./{{$prefix}}-{{$env}}-data"
  data = {
    project = {
      project_id = "{{$prefix}}-{{$env}}-data"
      apis = [
        "bigquery.googleapis.com",
        "compute.googleapis.com",
        "servicenetworking.googleapis.com",
        "sqladmin.googleapis.com",
      ]
      shared_vpc_attachment = {
        host_project_id = "{{$prefix}}-{{$env}}-networks"
      }
    }
    # Step 5.2: uncomment and re-run the engine once all previous steps have been completed.
    /* terraform_addons = {
      raw_config = <<EOF
data "google_secret_manager_secret_version" "mystudies_db_default_password" {
  provider = google-beta
  secret  = "auto-mystudies-sql-default-user-password"
  project = "{{$prefix}}-{{$env}}-secrets"
}
EOF
    } */
    resources = {
      # Step 5.3: uncomment and re-run the engine once all previous steps have been completed.
      # cloud_sql_instances = [{
      #   name               = "mystudies"
      #   type               = "mysql"
      #   network_project_id = "{{$prefix}}-{{$env}}-networks"
      #   network            = "{{$prefix}}-{{$env}}-network"
      #   user_password      = "$${data.google_secret_manager_secret_version.mystudies_db_default_password.secret_data}"
      # }]
      iam_members = {
        "roles/cloudsql.client" = [
          "serviceAccount:bastion@{{$prefix}}-{{$env}}-networks.iam.gserviceaccount.com",
          "serviceAccount:auth-server-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:hydra-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:response-server-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-designer-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:study-metadata-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:user-registration-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:participant-manager-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
          "serviceAccount:triggers-pubsub-handler-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com",
        ]
        "roles/bigquery.jobUser" = [
          "serviceAccount:{{$prefix}}-{{$env}}-firebase@appspot.gserviceaccount.com",
        ]
        "roles/bigquery.dataEditor" = [
          "serviceAccount:{{$prefix}}-{{$env}}-firebase@appspot.gserviceaccount.com",
        ]
      }
      storage_buckets = [
        {
          name = "{{$prefix}}-{{$env}}-mystudies-consent-documents"
          iam_members = [{
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:user-registration-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com"
          },{
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:participant-manager-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com"
          }]
        },
        {
          name = "{{$prefix}}-{{$env}}-mystudies-fda-resources"
          iam_members = [{
            role   = "roles/storage.objectAdmin"
            member = "serviceAccount:study-designer-gke-sa@{{$prefix}}-{{$env}}-apps.iam.gserviceaccount.com"
          }]
        },
        {
          name = "{{$prefix}}-{{$env}}-mystudies-sql-import"
          # Step 6: uncomment and re-run the engine once all previous steps have been completed.
          # iam_members = [{
          #   role   = "roles/storage.objectViewer"
          #   member = "serviceAccount:$${module.mystudies.instance_service_account_email_address}"
          # }]
        },
      ]
      bigquery_datasets = [{
        dataset_id = "{{$prefix}}_{{$env}}_mystudies_firestore_data"
      }]
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
  name     = "{{$prefix}}-{{$env}}-gke-cluster"
  location = "{{$default_location}}"
  project  = "{{$prefix}}-{{$env}}-apps"
}

provider "kubernetes" {
  load_config_file       = false
  token                  = data.google_client_config.default.access_token
  host                   = data.google_container_cluster.gke_cluster.endpoint
  client_certificate     = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_certificate)
  client_key             = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.client_key)
  cluster_ca_certificate = base64decode(data.google_container_cluster.gke_cluster.master_auth.0.cluster_ca_certificate)
}
EOF
    }
  }
}
