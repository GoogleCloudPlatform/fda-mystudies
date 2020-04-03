output "private_network" {
  value = module.private.network_self_link
}

output "gke_network" {
  value = module.gke_network.network_self_link
}
