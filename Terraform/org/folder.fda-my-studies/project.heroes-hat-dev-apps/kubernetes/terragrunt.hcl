include {
  path = find_in_parent_folders()
}

dependency "apps" {
  config_path = "../apps"

  mock_outputs = {
    gke_cluster = {
      name            = "mock-name"
      location        = "mock-location"
      service_account = "mock-service-account"
    }
    apps_service_accounts = {}
  }
}

dependency "data" {
  config_path  = "../../project.heroes-hat-dev-data/data/"
  skip_outputs = true
}

inputs = {
  my_studies_cluster = {
    name            = dependency.apps.outputs.gke_cluster.name
    location        = dependency.apps.outputs.gke_cluster.location
    service_account = dependency.apps.outputs.gke_cluster.service_account
  }
  apps_service_accounts = dependency.apps.outputs.apps_service_accounts
}
