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

# This file contains Terraform resources to setup a bastion host VM to connect to the private
# CloudSQL, which includes:
# - A dedicated service account for the bastion host, with scope limited to CloudSQL service only,
# - A private compute instance with its compute instance template,
# - A firewall rule to allow TCP:22 SSH access from the IAP to the bastion,
# - Necessary IAM permissions to allow IAP and OS Logins from specified members,
# - A Cloud Router and Cloud NAT to allow bastion host talk to the internet to download necessary
#   tools during instance startup in order to connect to the private CloudSQL instance.

# To connect to the CloudSQL instance:
# $ gcloud compute ssh bastion-vm --zone=<var.zone> --project=<var.project_id>
# $ cloud_sql_proxy -instances=example-data-project:us-east1:my-studies=tcp:3306
# $ mysql -u default -p --host 127.0.0.1
#
# The bastion compute instance, service account, firewall rule, and IAM permissions setups.
module "bastion" {
  source = "terraform-google-modules/bastion-host/google"

  name         = "bastion-vm"
  host_project = var.project_id
  project      = var.project_id
  region       = var.region
  zone         = var.zone
  network      = module.private.network_self_link
  subnet       = module.private.subnets["${var.region}/${local.bastion_subnet_name}"].self_link
  image_family = "ubuntu-1804-lts"
  members      = var.bastion_users
  scopes = [
    "https://www.googleapis.com/auth/sqlservice.admin",
  ]
  startup_script = <<EOF
#!/bin/bash
sudo apt-get -y update
sudo apt-get -y install mysql-client-core-5.7
sudo wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O /usr/local/bin/cloud_sql_proxy
sudo chmod +x /usr/local/bin/cloud_sql_proxy
EOF
}

# Cloud Router and NAT to allow bastion-VM to connect to the Internet to install necessary tools.
# TODO: Move this to main.tf.
# TODO: Rename the module to "router" and nats to "nat" now that it's not bastion-specific.
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
      },
      {
        name                     = module.private.subnets["${var.region}/${local.gke_clusters_subnet_name}"].self_link
        source_ip_ranges_to_nat  = ["ALL_IP_RANGES"]
        secondary_ip_range_names = []
    }]
  }]
}
