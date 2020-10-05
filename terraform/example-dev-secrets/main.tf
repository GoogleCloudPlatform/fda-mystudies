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
    prefix = "example-dev-secrets"
  }
}

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

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 8.1.0"

  name                    = "example-dev-secrets"
  org_id                  = ""
  folder_id               = "0000000000"
  billing_account         = "XXXXXX-XXXXXX-XXXXXX"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "secretmanager.googleapis.com",
  ]
}

resource "google_secret_manager_secret" "manual_mystudies_wcp_user" {
  provider = google-beta

  secret_id = "manual-mystudies-wcp-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}


resource "google_secret_manager_secret" "manual_mystudies_wcp_password" {
  provider = google-beta

  secret_id = "manual-mystudies-wcp-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
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
        location = "us-central1"
      }
    }
  }
}


resource "google_secret_manager_secret" "auto_auth_server_db_password" {
  provider = google-beta

  secret_id = "auto-auth-server-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_db_password.id
  secret_data = random_password.passwords["auth_server_db_password"].result
}

resource "google_secret_manager_secret" "auto_auth_server_db_user" {
  provider = google-beta

  secret_id = "auto-auth-server-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_auth_server_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_auth_server_db_user.id
  secret_data = random_string.strings["auth_server_db_user"].result
}

resource "google_secret_manager_secret" "auto_hydra_db_password" {
  provider = google-beta

  secret_id = "auto-hydra-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
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
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_hydra_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_hydra_db_user.id
  secret_data = random_string.strings["hydra_db_user"].result
}

resource "google_secret_manager_secret" "auto_hydra_secrets_system" {
  provider = google-beta

  secret_id = "auto-hydra-secrets-system"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_hydra_secrets_system_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_hydra_secrets_system.id
  secret_data = random_password.secrets["hydra_secrets_key"].result
}

resource "google_secret_manager_secret" "auto_mystudies_ma_client_id" {
  provider = google-beta

  secret_id = "auto-mystudies-ma-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_ma_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_ma_client_id.id
  secret_data = random_string.strings["mystudies_ma_client_id"].result
}

resource "google_secret_manager_secret" "auto_mystudies_ma_secret_key" {
  provider = google-beta

  secret_id = "auto-mystudies-ma-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_ma_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_ma_secret_key.id
  secret_data = random_password.passwords["mystudies_ma_secret_key"].result
}

resource "google_secret_manager_secret" "auto_mystudies_rs_client_id" {
  provider = google-beta

  secret_id = "auto-mystudies-rs-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_rs_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_rs_client_id.id
  secret_data = random_string.strings["mystudies_rs_client_id"].result
}

resource "google_secret_manager_secret" "auto_mystudies_rs_secret_key" {
  provider = google-beta

  secret_id = "auto-mystudies-rs-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_rs_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_rs_secret_key.id
  secret_data = random_password.passwords["mystudies_rs_secret_key"].result
}

resource "google_secret_manager_secret" "auto_mystudies_sql_default_user_password" {
  provider = google-beta

  secret_id = "auto-mystudies-sql-default-user-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_sql_default_user_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_sql_default_user_password.id
  secret_data = random_password.passwords["mystudies_sql_default_user_password"].result
}

resource "google_secret_manager_secret" "auto_mystudies_urs_client_id" {
  provider = google-beta

  secret_id = "auto-mystudies-urs-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_urs_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_urs_client_id.id
  secret_data = random_string.strings["mystudies_urs_client_id"].result
}

resource "google_secret_manager_secret" "auto_mystudies_urs_secret_key" {
  provider = google-beta

  secret_id = "auto-mystudies-urs-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_urs_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_urs_secret_key.id
  secret_data = random_password.passwords["mystudies_urs_secret_key"].result
}

resource "google_secret_manager_secret" "auto_mystudies_wcp_client_id" {
  provider = google-beta

  secret_id = "auto-mystudies-wcp-client-id"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_wcp_client_id_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_wcp_client_id.id
  secret_data = random_string.strings["mystudies_wcp_client_id"].result
}

resource "google_secret_manager_secret" "auto_mystudies_wcp_secret_key" {
  provider = google-beta

  secret_id = "auto-mystudies-wcp-secret-key"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_mystudies_wcp_secret_key_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_mystudies_wcp_secret_key.id
  secret_data = random_password.passwords["mystudies_wcp_secret_key"].result
}

resource "google_secret_manager_secret" "auto_response_server_db_password" {
  provider = google-beta

  secret_id = "auto-response-server-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_server_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_server_db_password.id
  secret_data = random_password.passwords["response_server_db_password"].result
}

resource "google_secret_manager_secret" "auto_response_server_db_user" {
  provider = google-beta

  secret_id = "auto-response-server-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_response_server_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_response_server_db_user.id
  secret_data = random_string.strings["response_server_db_user"].result
}

resource "google_secret_manager_secret" "auto_study_designer_db_password" {
  provider = google-beta

  secret_id = "auto-study-designer-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_designer_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_designer_db_password.id
  secret_data = random_password.passwords["study_designer_db_password"].result
}

resource "google_secret_manager_secret" "auto_study_designer_db_user" {
  provider = google-beta

  secret_id = "auto-study-designer-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_designer_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_designer_db_user.id
  secret_data = random_string.strings["study_designer_db_user"].result
}

resource "google_secret_manager_secret" "auto_study_metadata_db_password" {
  provider = google-beta

  secret_id = "auto-study-metadata-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_metadata_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_metadata_db_password.id
  secret_data = random_password.passwords["study_metadata_db_password"].result
}

resource "google_secret_manager_secret" "auto_study_metadata_db_user" {
  provider = google-beta

  secret_id = "auto-study-metadata-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_study_metadata_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_study_metadata_db_user.id
  secret_data = random_string.strings["study_metadata_db_user"].result
}

resource "google_secret_manager_secret" "auto_user_registration_db_password" {
  provider = google-beta

  secret_id = "auto-user-registration-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_user_registration_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_user_registration_db_password.id
  secret_data = random_password.passwords["user_registration_db_password"].result
}

resource "google_secret_manager_secret" "auto_user_registration_db_user" {
  provider = google-beta

  secret_id = "auto-user-registration-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_user_registration_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_user_registration_db_user.id
  secret_data = random_string.strings["user_registration_db_user"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_db_password" {
  provider = google-beta

  secret_id = "auto-participant-manager-db-password"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_db_password_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_db_password.id
  secret_data = random_password.passwords["participant_manager_db_password"].result
}

resource "google_secret_manager_secret" "auto_participant_manager_db_user" {
  provider = google-beta

  secret_id = "auto-participant-manager-db-user"
  project   = module.project.project_id

  replication {
    user_managed {
      replicas {
        location = "us-central1"
      }
    }
  }
}

resource "google_secret_manager_secret_version" "auto_participant_manager_db_user_data" {
  provider = google-beta

  secret      = google_secret_manager_secret.auto_participant_manager_db_user.id
  secret_data = random_string.strings["participant_manager_db_user"].result
}
