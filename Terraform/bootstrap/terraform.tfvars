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

devops_project_id = "validcare-research-devops"
org_id            = "423192334367"
// Uncomment in order to limit changes to folder.
// folder_id         = "346263855056"
billing_account  = "00C4F7-942DBB-FE88B3"
state_bucket     = "validcare-research-terraform-state-09768"
storage_location = "us-central1"
org_admin        = "group:vr-gcp-admins@validcare.com"
devops_owners = [
  "group:vr-gcp-admins@validcare.com",
]
