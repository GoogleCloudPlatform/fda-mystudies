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
    prefix           = "btcsoft"
    # Environment label that will be appended to PREFIX in your project and resource names
    # For example, "dev"
    env              = "dev"
    # Id of the folder you are deploying into
    # In the form of "0000000000000"
    folder_id        = "274914618000"
    # Billing account that your projects will be attached to
    # In the form of "XXXXXX-XXXXXX-XXXXXX"
    billing_account  = "010BB2-E7A763-738CAE"
    # Domain that your applications URLs will belong to
    # For example, "example.com"
    domain           = "boston-technology.com"
    # Default cloud region that your resources will be created in
    # For example, "us-central1"
    default_location = "us-east1"
    # Default zone within that region that your resources will be created in
    # For example, "a"
    default_zone     = "b"
    # The account or organization that your cloned github repo belongs to 
    # For example, "GoogleCloudPlatform"
    github_owner     = "boston-tech"
    # The name of your cloned github repo 
    # For example, "fda-mystudies"
    github_repo      = "develop-fda-mystudies"
    # The branch of your cloned repo that your CICD pipelines will monitor
    # For example, "develop"
    github_branch    = "btc-dev2dev-deployment"
    # GKE master authorized networks.
    # Comment out this block if you would like to allow connections from anywhere.
    master_authorized_networks = [
      {
        cidr_block   = "0.0.0.0/0"
        display_name = "btcsoft-dev-ip"
      },
    ]
  }
}
