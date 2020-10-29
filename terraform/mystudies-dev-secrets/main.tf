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
    bucket = "mystudies-dev-terraform-state"
    prefix = "mystudies-dev-secrets"
  }
}

resource "random_string" "strings" {
  for_each = toset([
    "auth_server_db_user",
    "auth_server_client_id",
    "response_datastore_db_user",
    "response_datastore_client_id",
    "study_builder_db_user",
    "study_builder_client_id",
    "study_datastore_db_user",
    "study_datastore_client_id",
    "participant_consent_datastore_db_user",
    "participant_consent_datastore_client_id",
    "participant_enroll_datastore_db_user",
    "participant_enroll_datastore_client_id",
    "participant_user_datastore_db_user",
    "participant_user_datastore_client_id",
    "participant_manager_datastore_db_user",
    "participant_manager_datastore_client_id",
    "hydra_db_user",
  ])
  length  = 16
  special = true
}

resource "random_password" "passwords" {
  for_each = toset([
    "mystudies_sql_default_user_password",
    "auth_server_db_password",
    "auth_server_secret_key",
    "response_datastore_db_password",
    "response_datastore_secret_key",
    "study_builder_db_password",
    "study_builder_secret_key",
    "study_datastore_db_password",
    "study_datastore_secret_key",
    "participant_consent_datastore_db_password",
    "participant_consent_datastore_secret_key",
    "participant_enroll_datastore_db_password",
    "participant_enroll_datastore_secret_key",
    "participant_user_datastore_db_password",
    "participant_user_datastore_secret_key",
    "participant_manager_datastore_db_password",
    "participant_manager_datastore_secret_key",
    "hydra_db_password",
  ])
  length  = 16
  special = true
}

resource "random_password" "system_secrets" {
  for_each = toset([
    "hydra_system_secret",
  ])
  length  = 32
  special = false
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 9.1.0"

  name                    = "mystudies-dev-secrets"
  org_id                  = ""
  folder_id               = "440087619763"
  billing_account         = "01B494-31B256-17B2A6"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "secretmanager.googleapis.com",
  ]
}

resource "google_secret_manager_secret" "manual_study_builder_user" {
  provider = google-beta

  secret_id = "manual-study-builder-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_study_builder_password" {
  provider = google-beta

  secret_id = "manual-study-builder-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_email_address" {
  provider = google-beta

  secret_id = "manual-mystudies-email-address"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_email_password" {
  provider = google-beta

  secret_id = "manual-mystudies-email-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_from_email_address" {
  provider = google-beta

  secret_id = "manual-mystudies-from-email-address"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_contact_email_address" {
  provider = google-beta

  secret_id = "manual-mystudies-contact-email-address"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_from_email_domain" {
  provider = google-beta

  secret_id = "manual-mystudies-from-email-domain"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_smtp_hostname" {
  provider = google-beta

  secret_id = "manual-mystudies-smtp-hostname"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_smtp_use_ip_allowlist" {
  provider = google-beta

  secret_id = "manual-mystudies-smtp-use-ip-allowlist"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_log_path" {
  provider = google-beta

  secret_id = "manual-log-path"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_org_name" {
  provider = google-beta

  secret_id = "manual-org-name"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_terms_url" {
  provider = google-beta

  secret_id = "manual-terms-url"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_privacy_url" {
  provider = google-beta

  secret_id = "manual-privacy-url"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_fcm_api_url" {
  provider = google-beta

  secret_id = "manual-fcm-api-url"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mobile_app_appid" {
  provider = google-beta

  secret_id = "manual-mobile-app-appid"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_android_bundle_id" {
  provider = google-beta

  secret_id = "manual-android-bundle-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_android_server_key" {
  provider = google-beta

  secret_id = "manual-android-server-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_ios_bundle_id" {
  provider = google-beta

  secret_id = "manual-ios-bundle-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_ios_certificate" {
  provider = google-beta

  secret_id = "manual-ios-certificate"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_ios_certificate_password" {
  provider = google-beta

  secret_id = "manual-ios-certificate-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}


resource "google_secret_manager_secret" "auto_mystudies_sql_default_user_password" {
  provider = google-beta

  secret_id = "auto-mystudies-sql-default-user-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_sql_default_user_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_sql_default_user_password.id
  secret_data = random_password.passwords["mystudies_sql_default_user_password"].result
}

resource "google_secret_manager_secret" "auto_hydra_db_password" {
  provider = google-beta

  secret_id = "auto-hydra-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_hydra_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_hydra_db_password.id
  secret_data = random_password.passwords["hydra_db_password"].result
}

resource "google_secret_manager_secret" "auto_hydra_db_user" {
  provider = google-beta

  secret_id = "auto-hydra-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_hydra_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_hydra_db_user.id
  secret_data = random_string.strings["hydra_db_user"].result
}

resource "google_secret_manager_secret" "auto_hydra_system_secret" {
  provider = google-beta

  secret_id = "auto-hydra-system-secret"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_hydra_system_secret_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_hydra_system_secret.id
  secret_data = random_password.system_secrets["hydra_system_secret"].result
}

resource "google_secret_manager_secret" "auto_auth_server_db_user" {
  provider = google-beta

  secret_id = "auto-auth-server-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_db_user.id
  secret_data = random_string.strings["auth_server_db_user"].result
}

resource "google_secret_manager_secret" "auto_auth_server_db_password" {
  provider = google-beta

  secret_id = "auto-auth-server-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_db_password.id
  secret_data = random_password.passwords["auth_server_db_password"].result
}

resource "google_secret_manager_secret" "auto_auth_server_client_id" {
  provider = google-beta

  secret_id = "auto-auth-server-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_client_id.id
  secret_data = random_string.strings["auth_server_client_id"].result
}

resource "google_secret_manager_secret" "auto_auth_server_secret_key" {
  provider = google-beta

  secret_id = "auto-auth-server-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_secret_key.id
  secret_data = random_password.passwords["auth_server_secret_key"].result
}

resource "google_secret_manager_secret" "auto_response_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-response-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_datastore_db_user.id
  secret_data = random_string.strings["response_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_response_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-response-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_datastore_db_password.id
  secret_data = random_password.passwords["response_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_response_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-response-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_datastore_client_id.id
  secret_data = random_string.strings["response_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_response_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-response-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_datastore_secret_key.id
  secret_data = random_password.passwords["response_datastore_secret_key"].result
}

resource "google_secret_manager_secret" "auto_study_builder_db_user" {
  provider = google-beta

  secret_id = "auto-study-builder-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_builder_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_builder_db_user.id
  secret_data = random_string.strings["study_builder_db_user"].result
}

resource "google_secret_manager_secret" "auto_study_builder_db_password" {
  provider = google-beta

  secret_id = "auto-study-builder-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_builder_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_builder_db_password.id
  secret_data = random_password.passwords["study_builder_db_password"].result
}

resource "google_secret_manager_secret" "auto_study_builder_client_id" {
  provider = google-beta

  secret_id = "auto-study-builder-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_builder_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_builder_client_id.id
  secret_data = random_string.strings["study_builder_client_id"].result
}

resource "google_secret_manager_secret" "auto_study_builder_secret_key" {
  provider = google-beta

  secret_id = "auto-study-builder-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_builder_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_builder_secret_key.id
  secret_data = random_password.passwords["study_builder_secret_key"].result
}

resource "google_secret_manager_secret" "auto_study_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-study-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_datastore_db_user.id
  secret_data = random_string.strings["study_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_study_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-study-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_datastore_db_password.id
  secret_data = random_password.passwords["study_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_study_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-study-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_datastore_client_id.id
  secret_data = random_string.strings["study_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_study_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-study-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_datastore_secret_key.id
  secret_data = random_password.passwords["study_datastore_secret_key"].result
}

resource "google_secret_manager_secret" "auto_participant_consent_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-participant-consent-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_consent_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_consent_datastore_db_user.id
  secret_data = random_string.strings["participant_consent_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_participant_consent_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-participant-consent-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_consent_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_consent_datastore_db_password.id
  secret_data = random_password.passwords["participant_consent_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_participant_consent_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-participant-consent-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_consent_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_consent_datastore_client_id.id
  secret_data = random_string.strings["participant_consent_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_participant_consent_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-participant-consent-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_consent_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_consent_datastore_secret_key.id
  secret_data = random_password.passwords["participant_consent_datastore_secret_key"].result
}

resource "google_secret_manager_secret" "auto_participant_enroll_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-participant-enroll-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_enroll_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_enroll_datastore_db_user.id
  secret_data = random_string.strings["participant_enroll_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_participant_enroll_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-participant-enroll-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_enroll_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_enroll_datastore_db_password.id
  secret_data = random_password.passwords["participant_enroll_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_participant_enroll_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-participant-enroll-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_enroll_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_enroll_datastore_client_id.id
  secret_data = random_string.strings["participant_enroll_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_participant_enroll_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-participant-enroll-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_enroll_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_enroll_datastore_secret_key.id
  secret_data = random_password.passwords["participant_enroll_datastore_secret_key"].result
}

resource "google_secret_manager_secret" "auto_participant_user_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-participant-user-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_user_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_user_datastore_db_user.id
  secret_data = random_string.strings["participant_user_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_participant_user_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-participant-user-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_user_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_user_datastore_db_password.id
  secret_data = random_password.passwords["participant_user_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_participant_user_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-participant-user-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_user_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_user_datastore_client_id.id
  secret_data = random_string.strings["participant_user_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_participant_user_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-participant-user-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_user_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_user_datastore_secret_key.id
  secret_data = random_password.passwords["participant_user_datastore_secret_key"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_datastore_db_user" {
  provider = google-beta

  secret_id = "auto-participant-manager-datastore-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_datastore_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_datastore_db_user.id
  secret_data = random_string.strings["participant_manager_datastore_db_user"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_datastore_db_password" {
  provider = google-beta

  secret_id = "auto-participant-manager-datastore-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_datastore_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_datastore_db_password.id
  secret_data = random_password.passwords["participant_manager_datastore_db_password"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_datastore_client_id" {
  provider = google-beta

  secret_id = "auto-participant-manager-datastore-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_datastore_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_datastore_client_id.id
  secret_data = random_string.strings["participant_manager_datastore_client_id"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_datastore_secret_key" {
  provider = google-beta

  secret_id = "auto-participant-manager-datastore-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-east"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_datastore_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_datastore_secret_key.id
  secret_data = random_password.passwords["participant_manager_datastore_secret_key"].result
}
