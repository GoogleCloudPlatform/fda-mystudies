terraform {
  backend "gcs" {}
}

resource "google_project_iam_member" "sql_client_service_accounts" {
  for_each = toset(var.sql_client_service_accounts)

  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${each.key}"
}
