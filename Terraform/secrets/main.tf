terraform {
  backend "gcs" {
    bucket = "heroes-hat-dev-terraform-state-08679"
    prefix = "secrets"
  }
}

resource "google_secret_manager_secret" "secrets" {
  provider = google-beta

  for_each = toset([
    "my-studies-sql-default-user-password",
  ])

  secret_id = each.key
  project   = var.project_id

  replication {
    automatic = true
  }
}
