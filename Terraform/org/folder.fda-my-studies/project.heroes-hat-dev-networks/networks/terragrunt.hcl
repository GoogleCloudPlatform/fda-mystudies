include {
  path = find_in_parent_folders()
}

dependency "project" {
  config_path  = "../project"
  skip_outputs = true
}

dependency "service_project_apps" {
  config_path = "../../project.heroes-hat-dev-apps/project"

  mock_outputs = {
    project_id     = "mock-apps-project"
    project_number = "mock-apps-123"
  }
}

dependency "service_project_data" {
  config_path = "../../project.heroes-hat-dev-data/project"

  mock_outputs = {
    project_id     = "mock-data-project"
    project_number = "mock-data-123"
  }
}

inputs = {
  service_projects = [
    {
      id      = dependency.service_project_apps.outputs.project_id
      num     = dependency.service_project_apps.outputs.project_number
      has_gke = true
    },
    {
      id      = dependency.service_project_data.outputs.project_id
      num     = dependency.service_project_data.outputs.project_number
      has_gke = false
    },
  ]
}
