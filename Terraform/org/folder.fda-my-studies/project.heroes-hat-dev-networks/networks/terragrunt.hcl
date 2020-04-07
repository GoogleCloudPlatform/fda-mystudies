include {
  path = find_in_parent_folders()
}

dependency "project" {
  config_path  = "../project"
  skip_outputs = true
}

dependency "shared_vpc" {
config_path  = "../shared_vpc"
  skip_outputs = true
}
