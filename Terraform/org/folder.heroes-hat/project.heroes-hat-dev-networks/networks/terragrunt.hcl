dependency "project" {
  config_path = "../project"
  skip_outputs = true
}

dependency "service_project_apps" {
  config_path = "../../project.heroes-hat-dev-apps/project"

  mock_outputs = {
    project_id = "mock-apps-project"
  }
}

dependency "service_project_data" {
  config_path = "../../project.heroes-hat-dev-data/project"

  mock_outputs = {
    project_id = "mock-data-project"
  }
}

inputs = {
  service_projects = [
    dependency.service_project_apps.outputs.project_id,
    service_project_data = dependency.service_project_data.outputs.project_id,
  ]
}
