dependency "project" {
  config_path = "../project"
  skip_outputs = true
}

dependency "network" {
  config_path: "../../project.heroes-hat-dev-networks/networks"

  mock_outputs = {
    private_network = "mock-network"
  }

  inputs = {
    network = dependency.network.outputs.private_network
  }
}
