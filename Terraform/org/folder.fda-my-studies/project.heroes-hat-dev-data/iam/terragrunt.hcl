include {
  path = find_in_parent_folders()
}

dependency "project" {
  config_path  = "../project"
  skip_outputs = true
}

dependency "apps" {
  config_path = "../../project.heroes-hat-dev-apps/apps"

  mock_outputs = {
    service_account = "mock-gke-service-account"
  }
}

dependency "networks" {
  config_path = "../../project.heroes-hat-dev-networks/networks"

  mock_outputs = {
    bastion_service_account = "mock-bastion-service-account"
  }
}

inputs = {
  sql_client_service_accounts = [
    dependency.apps.outputs.service_account,
    dependency.networks.outputs.bastion_service_account,
  ]
}
