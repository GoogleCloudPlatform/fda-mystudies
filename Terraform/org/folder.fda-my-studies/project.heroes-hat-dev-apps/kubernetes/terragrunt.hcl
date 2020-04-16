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
    instance_user_password = "mock-db-user-password"
  }
}

inputs = {
  sql_instance_name = dependency.data.outputs.instance_name
  sql_instance_user = dependency.data.outputs.instance_user
  sql_instance_user_password = dependency.data.outputs.instance_user_password
  my_studies_cluster = {
    name            = dependency.apps.outputs.gke_cluster.name
    location        = dependency.apps.outputs.gke_cluster.location
    service_account = dependency.apps.outputs.gke_cluster.service_account
  }
}
