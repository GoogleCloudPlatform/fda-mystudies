variable "project_id" {
  type = string
}

variable "sql_client_service_accounts" {
  description = "Clients who have access to the SQL instances in this project"
  type        = list(string)
}
