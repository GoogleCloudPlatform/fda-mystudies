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
    service_account = "mock-service-account"
  }
}

inputs = {
  gke_service_account = dependency.apps.outputs.service_account
}
