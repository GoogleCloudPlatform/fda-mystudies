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
# $ mysql -h <sql_internal_ip> -u root
module "bastion" {
  source = "terraform-google-modules/bastion-host/google"

  name           = "bastion-vm"
  host_project   = var.project_id
  project        = var.project_id
  region         = var.region
  zone           = var.zone
  network        = module.private.network_self_link
  subnet         = module.private.subnets["${var.region}/${local.bastion_subnet_name}"].self_link
  image_family   = "ubuntu-1804-lts"
  members        = var.bastion_users
  scopes         = [
    "https://www.googleapis.com/auth/sqlservice.admin",
  ]
  startup_script = <<EOF
#!/bin/bash
sudo apt-get -y update
sudo apt-get -y install mysql-client-core-5.7
wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
chmod +x ./cloudsql_sql_proxy
EOF
}

# NAT to allow bastion-VM to connect to the Internet to install `mysql-client-core-5.7`.
module "bastion_router" {
  source  = "terraform-google-modules/cloud-router/google"
  name    = "bastion-router"
  region  = var.region
  project = var.project_id
  network = module.private.network_name
  nats = [{
    name                               = "bastion-nat"
    source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"
    subnetworks = [{
      name                     = module.private.subnets["${var.region}/${local.bastion_subnet_name}"].self_link
      source_ip_ranges_to_nat  = ["PRIMARY_IP_RANGE"]
      secondary_ip_range_names = []
    }]
  }]
}
