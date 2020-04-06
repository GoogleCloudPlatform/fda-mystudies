terraform {
  backend "gcs" {}
}

resource "google_firebase_project" "firebase" {
  provider = google-beta
  project  = var.project_id
}
