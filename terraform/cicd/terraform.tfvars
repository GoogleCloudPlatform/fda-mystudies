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

billing_account = "01B494-31B256-17B2A6"
project_id      = "mystudies-dev-devops"
state_bucket    = "mystudies-dev-terraform-state"
terraform_root  = "terraform"
build_viewers = [
  "group:dpt-dev@hcls.joonix.net",
]
