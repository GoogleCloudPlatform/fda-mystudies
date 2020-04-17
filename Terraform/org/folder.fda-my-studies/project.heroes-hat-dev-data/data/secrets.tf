# Create a separate DB user for each app, generate a password, and store in secrets
locals {
  apps = [
    "auth-server",
    "response-server",
    "study-designer",
    "study-meta-data",
    "user-registration",
  ]
}

resource "random_password" "db_passwords" {
  for_each = toset(local.apps)

  length  = 16
  special = true
}

resource "google_sql_user" "db_users" {
  for_each = toset(local.apps)

  name     = "${each.key}-db-user"
  instance = module.my_studies_cloudsql.instance_name
  host     = "%"
  password = random_password.db_passwords[each.key].result
  project  = var.project_id
}

resource "google_secret_manager_secret" "db_passwords_secrets" {
  provider = google-beta

  for_each = toset(local.apps)

  secret_id = "${each.key}-db-password"
  project   = var.secrets_project_id

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret" "db_users_secrets" {
  provider = google-beta

  for_each = toset(local.apps)

  secret_id = "${each.key}-db-user"
  project   = var.secrets_project_id

  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "db_passwords_secrets_values" {
  provider = google-beta

  for_each = toset(local.apps)

  secret      = google_secret_manager_secret.db_passwords_secrets[each.key].id
  secret_data = random_password.db_passwords[each.key].result
}

resource "google_secret_manager_secret_version" "db_users_secrets_values" {
  provider = google-beta

  for_each = toset(local.apps)

  secret      = google_secret_manager_secret.db_users_secrets[each.key].id
  secret_data = google_sql_user.db_users[each.key].name
}
