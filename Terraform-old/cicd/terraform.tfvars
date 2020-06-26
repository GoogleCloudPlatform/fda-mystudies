# Copyright 2020 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

org_id = "707577601068"
// Uncomment in order to limit changes to folder.
// folder_id         = "440087619763"
billing_account               = "01EA90-3519E1-89CB1F"
project_id                    = "heroes-hat-dev-devops"
state_bucket                  = "heroes-hat-dev-terraform-state-08679"
repo_owner                    = "GoogleCloudPlatform"
repo_name                     = "fda-mystudies"
branch_regex                  = "^early-access$"
continuous_deployment_enabled = true
trigger_enabled               = true
terraform_root                = "Terraform"
build_viewers = [
  "group:rocketturtle-gcp-admin@rocketturtle.net",
]
