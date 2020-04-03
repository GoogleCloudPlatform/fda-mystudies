variable "project_id" {
  description = "The GCP project id"
  type        = string
}

variable "region" {
  description = "The region to host the clusters in"
  default     = "us-central1"
  type        = string
}

variable "network_name" {
  description = "The name of the network to use for the clusters"
  type        = string
  default     = "default"
}

