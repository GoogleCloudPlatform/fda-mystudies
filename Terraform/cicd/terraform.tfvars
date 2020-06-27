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

org_id = "423192334367"
// Uncomment in order to limit changes to folder.
// folder_id         = "346263855056"
billing_account               = "00C4F7-942DBB-FE88B3"
project_id                    = "validcare-research-devops"
state_bucket                  = "validcare-research-terraform-state-09768"
repo_owner                    = "validcare"
repo_name                     = "validcare-mystudies"
branch_regex                  = "^Prod-Release$"
continuous_deployment_enabled = true
trigger_enabled               = true
terraform_root                = "Terraform"
build_viewers = [
  "group:vr-gcp-admins@validcare.com",
]
