variable "project_id" {
  description = "The GCP project id"
  type        = string
}

variable "secrets_project_id" {
  type = string
}

variable "cluster_name" {
  description = "The GKE cluster name"
  type        = string
}

variable "cluster_location" {
  description = "The GKE cluster location (region if regional, zone otherwise)"
  type        = string
}
