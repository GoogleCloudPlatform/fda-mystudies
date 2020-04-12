variable "project_id" {
  type = string
}

variable "region" {
  description = "The region where the network and subnets will be created for the GKE clusters and bastion host"
  type        = string
}

variable "zone" {
  description = "The zone where to create the bastion host"
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

variable "bastion_users" {
  description = "List of IAM resources to allow access to the bastion VM instance"
  default     = []
}
