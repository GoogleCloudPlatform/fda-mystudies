terraform {
  backend "gcs" {}
}

resource "google_project_iam_member" "sql_clients" {
  for_each = toset(var.sql_clients)

  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${each.key}"
}
