variable "project_id" {
  type = string
}

variable "gke_service_account" {
  description = "The service account used by the GKE cluster"
  type        = string
}

