# Top level template to instantiate MyStudies template with organization
# and study specific values.
template "mystudies" {
  # MyStudies template.
  recipe_path = "./mystudies.hcl"
  # The following values are placeholder values, change and adjust them according to
  # your use case and organization needs.
  data = {
    prefix           = "example"
    env              = "dev"
    folder_id        = "0000000000"
    billing_account  = "XXXXXX-XXXXXX-XXXXXX"
    domain           = "example.com"
    default_location = "us-central1"
    default_zone     = "a"
    github_owner     = "GoogleCloudPlatform"
    github_repo      = "example"
    github_branch    = "master"
    # GKE master authorized networks.
    # Comment out this block if you would like to allow connections from anywhere.
    master_authorized_networks = [
      {
        cidr_block   = "0.0.0.0/0"
        display_name = "Example diplay name"
      },
    ]
  }
}
