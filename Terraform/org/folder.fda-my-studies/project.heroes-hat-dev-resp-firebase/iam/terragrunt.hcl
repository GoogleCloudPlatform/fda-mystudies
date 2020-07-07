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

include {
  path = find_in_parent_folders()
}

dependency "project" {
  config_path  = "../project"
  skip_outputs = true
}

dependency "apps" {
  config_path = "../../project.heroes-hat-dev-apps/apps"

  mock_outputs = {
    service_account = "mock-gke-service-account"
    apps_service_accounts = {
      response-server = {
        email = "mock-app-gke@mock-project.iam.gserviceaccount.com"
      }
    }
  }
}

inputs = {
  datastore_users = ["serviceAccount:${dependency.apps.outputs.apps_service_accounts["response-server"].email}"]
}
