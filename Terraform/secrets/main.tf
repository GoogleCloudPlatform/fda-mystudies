resource "google_secret_manager_secret" "my_studies_sql_default_user_password" {
  provider = google-beta

  secret_id = "my-studies-sql-default-user-password"
  project   = var.project_id

  replication {
    automatic = true
  }
}
