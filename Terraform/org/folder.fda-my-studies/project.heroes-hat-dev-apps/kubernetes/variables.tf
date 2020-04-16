variable "project_id" {
  description = "The GCP project id"
  type        = string
}

variable "sql_instance_name" {
  description = "The name of the SQL instance"
  type        = string
}

variable "sql_instance_user" {
  description = "The name of the user to use to log into the SQL instance"
  type        = string
}

variable "sql_instance_user_password" {
  description = "The password for the user used to log into the SQL instance"
  type        = string
}

variable "secrets_project_id" {
  type = string
}

variable "my_studies_cluster" {
  description = "The GKE cluster module"
  type = object({
    name            = string
    location        = string
    service_account = string
  })
}

variable "apps_service_accounts" {
  description = "Mapping of app name to app service account"
  type        = map
}
