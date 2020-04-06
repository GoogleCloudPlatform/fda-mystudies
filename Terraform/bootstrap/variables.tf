variable "project_id" {
  type = string
}

variable "org_id" {
  type = string
}

variable "billing_account" {
  type = string
}

variable "state_bucket" {
  type = string
}

variable "storage_location" {
  type = string
}

variable "repo_owner" {
  description = "Owner of the GitHub repo"
  type        = string
}

variable "repo_name" {
  description = "Name of the GitHub repo"
  type        = string
}

variable "cloudbuild_trigger_branch" {
  type    = string
  default = "master"
}

variable "continuous_deployment_enabled" {
  type    = bool
  default = false
}
