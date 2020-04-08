devops_project_id             = "heroes-hat-dev-devops"
org_id                        = "707577601068"
billing_account               = "01EA90-3519E1-89CB1F"
state_bucket                  = "heroes-hat-dev-terraform-state-08679"
storage_location              = "us-central1"
org_admin                     = "group:rocketturtle-gcp-admin@rocketturtle.net"
repo_owner                    = "GoogleCloudPlatform"
repo_name                     = "fda-mystudies"
cloudbuild_trigger_branch     = "terraform" # Change to "early-access" or the final prod branch.
continuous_deployment_enabled = true
devops_owners = [
    "group:rocketturtle-gcp-admin@rocketturtle.net",
]
