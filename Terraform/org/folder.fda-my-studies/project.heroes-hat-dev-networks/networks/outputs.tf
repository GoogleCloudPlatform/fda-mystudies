output "private_network" {
  value = module.private.network_self_link
}

output "gke_subnetwork" {
  value = module.private.subnets["${var.gke_region}/${local.gke_clusters_subnet_name}"].id
}
