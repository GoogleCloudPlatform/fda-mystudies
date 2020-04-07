variable "project_id" {
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
