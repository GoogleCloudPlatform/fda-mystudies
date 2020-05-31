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

# DNS sets up nameservers to connect to the GKE clusters.
module "dns" {
  source  = "terraform-google-modules/cloud-dns/google"
  version = "3.0.1"

  name       = var.dns_name
  project_id = var.project_id
  type       = "public"
  domain     = var.dns_domain

  recordsets = [{
    name = "tf-dev"
    type = "A"
    ttl  = 30
    records = [
      google_compute_global_address.ingress_static_ip.address,
    ]
  }]
}
