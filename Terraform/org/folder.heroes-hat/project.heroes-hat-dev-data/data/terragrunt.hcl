dependency "project" {
  config_path = "../project"
  skip_outputs = true
}

dependency "network" {
  config_path: "../../project.heroes-hat-dev-network/network"

  mock_outputs = {
    private_network = "mock-network"
  }

  inputs = {
    network = dependency.network.outputs.private_network
  }
}
