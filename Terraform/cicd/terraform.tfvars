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

org_id                        = "707577601068"
devops_project_id             = "heroes-hat-dev-devops"
state_bucket                  = "heroes-hat-dev-terraform-state-08679"
repo_owner                    = "GoogleCloudPlatform"
repo_name                     = "fda-mystudies"
cloudbuild_trigger_branch     = "terraform" # Change to "early-access" or the final prod branch.
continuous_deployment_enabled = true
trigger_enabled               = true
