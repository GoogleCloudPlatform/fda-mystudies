# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Top level template to instantiate MyStudies template with organization
# and study specific values.
template "mystudies" {
  # MyStudies template.
  recipe_path = "./mystudies.hcl"
  # The following values are placeholder values, change and adjust them according to
  # your use case and organization needs.
  data = {
    # Prefix that will be prepended to your project and resource names
    # For example, "mystudies"
    prefix           = "<PREFIX>"
    # Environment label that will be appended to PREFIX in your project and resource names
    # For example, "dev"
    env              = "<ENV>"
    # Id of the folder you are deploying into
    # In the form of "0000000000000"
    folder_id        = "<FOLDER_ID>"
    # Billing account that your projects will be attached to
    # In the form of "XXXXXX-XXXXXX-XXXXXX"
    billing_account  = "<BILLING_ACCOUNT>"
    # Domain that your applications URLs will belong to
    # For example, "example.com"
    domain           = "<DOMAIN>"
    # Default cloud region that your resources will be created in
    # For example, "us-central1"
    default_location = "<LOCATION>"
    # Default zone within that region that your resources will be created in
    # For example, "a"
    default_zone     = "<ZONE>"
    # The account or organization that your cloned github repo belongs to 
    # For example, "GoogleCloudPlatform"
    github_owner     = "<REPO_OWNER>"
    # The name of your cloned github repo 
    # For example, "fda-mystudies"
    github_repo      = "<REPO>"
    # The branch of your cloned repo that your CICD pipelines will monitor
    # For example, "develop"
    github_branch    = "<REPO_BRANCH>"
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
