variable "devops_project_id" {
  type = string
}

variable "devops_owners" {
  type = list(string)
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

variable "org_admin" {
  type = string
}
