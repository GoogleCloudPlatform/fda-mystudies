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

project_id                 = "heroes-hat-dev-apps"
network_project_id         = "heroes-hat-dev-networks"
gke_region                 = "us-east1"
cluster_name               = "heroes-hat-cluster"
master_authorized_networks = [{ cidr_block = "104.132.0.0/14", display_name = "Google Offices/Campuses/CorpDC" }]
repo_owner                 = "GoogleCloudPlatform"
repo_name                  = "fda-mystudies"
cloudbuild_trigger_branch  = "early-access"
dns_name                   = "heroes-hat"
dns_domain                 = "heroes-hat.rocketturtle.net."
