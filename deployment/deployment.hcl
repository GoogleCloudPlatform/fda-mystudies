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
    prefix           = "btc-track2"
    env              = "dev"
    folder_id        = "440087619763"
    billing_account  = "01EA90-3519E1-89CB1F"
    domain           = "example.com"
    default_location = "us-central1"
    default_zone     = "a"
    github_owner     = "GoogleCloudPlatform"
    github_repo      = "fda-mystudies"
    github_branch    = "btc-develop"
    # GKE master authorized networks.
    # Comment out this block if you would like to allow connections from anywhere.
    master_authorized_networks = [
      {
        cidr_block   = "0.0.0.0/0"
        display_name = "btc-track2"
      },
    ]
  }
}
