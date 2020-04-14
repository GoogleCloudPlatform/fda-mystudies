terraform {
  backend "gcs" {}
}

resource "google_project_iam_member" "gke_sql_access" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${var.gke_service_account}"
}
