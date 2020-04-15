variable "project_id" {
  type = string
}

variable "sql_clients" {
  description = "Clients who have access to the SQL instances in this project"
  type        = string
}
