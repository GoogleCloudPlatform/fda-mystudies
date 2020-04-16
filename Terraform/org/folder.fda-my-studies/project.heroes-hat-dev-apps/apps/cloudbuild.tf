# Run after install the Cloud Build app and connect GitHub repo in Cloud Build by following
# https://cloud.google.com/cloud-build/docs/automating-builds/create-github-app-triggers#installing_the_cloud_build_app.
resource "google_cloudbuild_trigger" "wcp" {
  # Disable for now as don't know when the owners want to generate new images.
  disabled = true
  provider = google-beta
  project  = var.project_id
  name     = "wcp"

  included_files = [
    "WCP/**"
  ]

  github {
    owner = var.repo_owner
    name  = var.repo_name
    push {
      branch = var.cloudbuild_trigger_branch
    }
  }

  filename = "WCP/cloudbuild.yaml"
}

resource "google_cloudbuild_trigger" "auth_server_ws" {
  # Disable for now as don't know when the owners want to generate new images.
  disabled = true
  provider = google-beta
  project  = var.project_id
  name     = "auth-server-ws"

  included_files = [
    "auth-server-ws/**"
  ]

  github {
    owner = var.repo_owner
    name  = var.repo_name
    push {
      branch = var.cloudbuild_trigger_branch
    }
  }

  filename = "auth-server-ws/cloudbuild.yaml"
}

resource "google_cloudbuild_trigger" "wcp_ws" {
  # Disable for now as don't know when the owners want to generate new images.
  disabled = true
  provider = google-beta
  project  = var.project_id
  name     = "wcp-ws"

  included_files = [
    "WCP-WS/**"
  ]

  github {
    owner = var.repo_owner
    name  = var.repo_name
    push {
      branch = var.cloudbuild_trigger_branch
    }
  }

  filename = "WCP-WS/cloudbuild.yaml"
}

resource "google_cloudbuild_trigger" "user_registration_server_ws" {
  # Disable for now as don't know when the owners want to generate new images.
  disabled = true
  provider = google-beta
  project  = var.project_id
  name     = "user-registration-server-ws"

  included_files = [
    "user-registration-server-ws/**"
  ]

  github {
    owner = var.repo_owner
    name  = var.repo_name
    push {
      branch = var.cloudbuild_trigger_branch
    }
  }

  filename = "user-registration-server-ws/cloudbuild.yaml"
}

resource "google_cloudbuild_trigger" "response_server_ws" {
  # Disable for now as don't know when the owners want to generate new images.
  disabled = true
  provider = google-beta
  project  = var.project_id
  name     = "response-server-ws"

  included_files = [
    "response-server-ws/**"
  ]

  github {
    owner = var.repo_owner
    name  = var.repo_name
    push {
      branch = var.cloudbuild_trigger_branch
    }
  }

  filename = "response-server-ws/cloudbuild.yaml"
}
