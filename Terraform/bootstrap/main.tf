# ========================================== STEP 2 BEGIN ==========================================
# TODO(user): Uncomment after deployment and run `terraform init`.
# terraform {
#   backend "gcs" {
#     bucket = "heroes-hat-dev-terraform-state-08679"
#     prefix = "bootstrap"
#   }
# }
# =========================================== STEP 2 END ===========================================

# ========================================== STEP 1 BEGIN ==========================================
locals {
  cloudbuild_apis = ["cloudbuild.googleapis.com"]
}

module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 7.0"

  name                    = var.project_id
  org_id                  = var.org_id
  billing_account         = var.billing_account
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
}

module "state_bucket" {
  source  = "terraform-google-modules/cloud-storage/google//modules/simple_bucket"
  version = "~> 1.4"

  name       = var.state_bucket
  project_id = var.project_id
  location   = var.storage_location
}

# Cloud Build - API
resource "google_project_service" "cloudbuild_apis" {
  for_each           = toset(local.cloudbuild_apis)
  project            = module.project.project_id
  service            = each.value
  disable_on_destroy = false
}
# =========================================== STEP 1 END ===========================================

# ========================================== STEP 3 BEGIN ==========================================

# TODO(user): Follow https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app
# to install the Cloud Build app and connect your GitHub repository to your Cloud project.

# =========================================== STEP 3 END ===========================================

# ========================================== STEP 4 BEGIN ==========================================
# TODO(user): Uncomment and run after install the Cloud Build app and connect GitHub repo in Cloud Build.
# resource "google_cloudbuild_trigger" "wcp" {
#   provider = google-beta
#   project  = module.project.project_id
#   name     = "wcp"

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "WCP/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "auth_server_ws" {
#   provider = google-beta
#   project  = module.project.project_id
#   name     = "auth-server-ws"

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "auth-server-ws/cloudbuild.yaml"
# }
# resource "google_cloudbuild_trigger" "wcp_ws" {
#   provider = google-beta
#   project  = module.project.project_id
#   name     = "wcp-ws"

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "WCP-WS/cloudbuild.yaml"
# }
# resource "google_cloudbuild_trigger" "user_registration_server_ws" {
#   provider = google-beta
#   project  = module.project.project_id
#   name     = "user-registration-server-ws"

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "user-registration-server-ws/cloudbuild.yaml"
# }

# resource "google_cloudbuild_trigger" "response_server_ws" {
#   provider = google-beta
#   project  = module.project.project_id
#   name     = "response-server-ws"

#   github {
#     owner = var.repo_owner
#     name  = var.repo_name
#     push {
#       branch = var.cloudbuild_trigger_branch
#     }
#   }

#   filename = "response-server-ws/cloudbuild.yaml"
# }
# =========================================== STEP 4 END ===========================================
