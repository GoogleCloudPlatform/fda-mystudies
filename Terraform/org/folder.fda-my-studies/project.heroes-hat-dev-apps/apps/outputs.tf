output "service_account" {
  value = module.heroes_hat_cluster.service_account
}

output "gke_cluster" {
  value = module.heroes_hat_cluster
}

output "apps_service_accounts" {
  value = google_service_account.apps_service_accounts
}
