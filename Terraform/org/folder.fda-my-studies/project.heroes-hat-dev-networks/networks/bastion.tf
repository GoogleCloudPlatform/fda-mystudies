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

# $ gcloud compute ssh bastion-vm --zone=<var.zone> --project=<var.project_id>
# $ sudo apt install mysql-client-core-5.7
# $ mysql -h <sql_internal_ip> -u root
module "iap_bastion" {
  source = "terraform-google-modules/bastion-host/google"

  name         = "bastion-vm"
  host_project = var.project_id
  project      = var.project_id
  region       = var.region
  zone         = var.zone
  network      = module.private.network_self_link
  subnet       = local.gke_subnet.self_link
  image_family = "ubuntu-1804-lts"
  members      = var.bastion_users
}

# Temporarily allow bastion-VM to connect to the Internet to install `mysql-client-core-5.7`.
# Remove after installation.
module "cloud-nat" {
  source  = "terraform-google-modules/cloud-router/google"
  name    = "bastion-router"
  region  = var.region
  project = var.project_id
  network = module.private.network_name
  nats = [{
    name                             = "bastion-nat"
    min_ports_per_vm                 = "64"
    udp_idle_timeout_sec             = "30"
    icmp_idle_timeout_sec            = "30"
    tcp_established_idle_timeout_sec = "1200"
    tcp_transitory_idle_timeout_sec  = "30"
  }]
}
