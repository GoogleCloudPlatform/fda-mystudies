include {
  path = find_in_parent_folders()
}

dependency "parent_folder" {
  config_path = "../../folder"

  mock_outputs = {
    name = "mock-folder"
  }
}

inputs = {
  folder_id = dependency.parent_folder.outputs.name
}
