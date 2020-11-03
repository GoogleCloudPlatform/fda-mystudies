# Top level template to instantiate MyStudies template with organization
# and study specific values.
template "mystudies" {
  # MyStudies template.
  recipe_path = "./mystudies.hcl"
  # The following values are placeholder values, change and adjust them according to
  # your use case and organization needs.
  data = {
    prefix           = "mystudies"
    env              = "dev"
    folder_id        = "440087619763"
    billing_account  = "0132B5-9CBD69-7F2F47"
    domain           = "hcls.joonix.net"
    default_location = "us-east1"
    default_zone     = "d"
    github_owner     = "zohrehj"
    github_repo      = "fda-mystudies"
    github_branch    = "develop"
    # GKE master authorized networks.
    # Comment out this block if you would like to allow connections from anywhere.
//    master_authorized_networks = [
//      {
//        cidr_block   = "0.0.0.0/0"
//        display_name = "Example diplay name"
//      },
//    ]
  }
}
