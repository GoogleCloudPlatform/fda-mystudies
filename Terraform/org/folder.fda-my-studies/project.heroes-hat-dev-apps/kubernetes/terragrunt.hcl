include {
  path = find_in_parent_folders()
}

dependency "apps" {
  config_path = "../apps"

  mock_outputs = {
    gke_cluster = {
      name            = "mock-name"
      location        = "mock-location"
      endpoint        = "mock-endpoint"
      ca_certificate  = "mock-ca-certificate"
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
    endpoint        = dependency.apps.outputs.gke_cluster.endpoint
    ca_certificate  = dependency.apps.outputs.gke_cluster.ca_certificate
  }
  apps_service_accounts = dependency.apps.outputs.apps_service_accounts
}
