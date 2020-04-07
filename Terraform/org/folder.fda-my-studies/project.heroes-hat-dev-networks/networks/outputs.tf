output "private_network" {
  value = module.private.network.network
}

output "gke_subnetwork" {
  value = local.gke_subnet
}
