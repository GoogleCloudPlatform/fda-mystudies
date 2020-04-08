include {
  path = find_in_parent_folders()
}

dependency "project" {
  config_path  = "../project"
  skip_outputs = true
}

dependency "data" {
  config_path  = "../../project.heroes-hat-dev-data/data/"
  skip_outputs = true
}

dependency "firebase" {
  config_path  = "../../project.heroes-hat-dev-resp-firebase/firebase/"
  skip_outputs = true
}

dependency "network" {
  config_path = "../../project.heroes-hat-dev-networks/networks/"

  mock_outputs = {
    network    = "mock-network"
    subnetwork = "mock-subnetwork"
  }
}
inputs = {
  network    = dependency.network.outputs.private_network.name
  subnetwork = dependency.network.outputs.gke_subnetwork.name
}
