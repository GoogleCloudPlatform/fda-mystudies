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
    apps_service_accounts = {
      mock-app = "mock-app-gke@mock-project.iam.gserviceaccount.com"
    }
  }
}

dependency "networks" {
  config_path = "../../project.heroes-hat-dev-networks/networks"

  mock_outputs = {
    bastion_service_account = "mock-bastion-service-account"
  }
}

inputs = {
  sql_client_service_accounts = concat(
    [
      dependency.apps.outputs.service_account,
      dependency.networks.outputs.bastion_service_account,
    ],
  values(dependency.apps.outputs.apps_service_accounts)[*].email)
}
