variable "project_id" {
  type = string
}

variable "service_projects" {
  type = list(object({
    id : string
    num : number
    has_gke : bool
  }))
}
