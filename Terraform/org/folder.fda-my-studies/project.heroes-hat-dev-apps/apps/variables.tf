variable "project_id" {
  description = "The GCP project id"
  type        = string
}

variable "network_project_id" {
  description = "The project ID of the network host project."
  type        = string
}

variable "gke_region" {
  description = "The region to host the clusters in"
  type        = string
}

variable "network" {
  description = "The network to use for the clusters"
  type        = string
  default     = "default"
}

variable "subnetwork" {
  description = "The subnetwork to use for the clusters"
  type        = string
  default     = "default"
}

variable "sql_instance_name" {
  description = "The name of the SQL instance"
  type        = string
}

variable "sql_instance_user" {
  description = "The name of the user to use to log into the SQL instance"
  type        = string
}

variable "secrets_project_id" {
  type = string
}
