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
  }
}

dependency "data" {
  config_path = "../../project.heroes-hat-dev-data/data/"

  mock_outputs = {
    instance_name = "mock-db"
    instance_user = "mock-db-user"
  }
}

inputs = {
  sql_instance_name  = dependency.data.outputs.instance_name
  sql_instance_user  = dependency.data.outputs.instance_user
  heroes_hat_cluster = dependency.apps.outputs.gke_cluster
}
