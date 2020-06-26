output "instance_name" {
  value = module.my_studies_cloudsql.instance_name
}

# This is not outputted by the safer_sql module, but allow dependents to treat
# it like it is.
output "instance_user" {
  value = "default"
}
