variable "project_id" {
  type = string
}

variable "gke_region" {
  description = "The region where the network and subnets will be created for the GKE clusters"
  type        = string
}

variable "gke_network_name" {
  description = "The name of the network that'll be used for the GKE clusters"
  type        = string
}

variable "service_projects" {
  type = list(object({
    id : string
    num : number
    has_gke : bool
  }))
}
