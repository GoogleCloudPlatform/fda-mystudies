variable "project_id" {
  description = "The GCP project id"
  type        = string
}

variable "secrets_project_id" {
  type = string
}

variable "my_studies_cluster" {
  description = "The GKE cluster module"
  type = object({
    name           = string
    location       = string
    endpoint       = string
    ca_certificate = string
  })
}

variable "apps_service_accounts" {
  description = "Mapping of app name to app service account"
  type        = map
}
