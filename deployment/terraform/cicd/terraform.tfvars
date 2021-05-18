# Copyright 2020 Google LLC
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

billing_account = "010BB2-E7A763-738CAE"
project_id      = "btcsoft-dev-devops"
state_bucket    = "btcsoft-dev-terraform-state"
terraform_root  = "deployment/terraform"
build_viewers = [
  "group:btcsoft-dev-cicd-viewers@boston-technology.com",
]
