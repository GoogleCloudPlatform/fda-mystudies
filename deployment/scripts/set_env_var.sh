# !/bin/bash

# Copyright 2020 Google LLC
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.

# This script sets environment variables that are used throughout the deployment

# PREFIX is a name you choose for your deployment that will be prepended to various
# directories, cloud resources and URLs (for example this could be `mystudies`)
export PREFIX=kyoto

# ENV is a label you choose that will be appended to PREFIX in your directories
# and cloud resources (for example this could be `dev`, `test` or `prod`)
export ENV=demo

# GIT_ROOT is the local path to the root of your cloned FDA MyStudies repository
export GIT_ROOT=/Users/hama/Work/01.IT/01.project/Abby/01.アジア治験NW/02.repo_clone/fda-mystudies

# LOCATION is the loctaion you specified for your deployment in
# deployment.hcl, for example `us-central1`
export LOCATION=asia-northeast1

# DOMAIN is the domain you will be using for your URLs (for example, 
# `your_company_name.com` or `your_medical_center.edu`)
export DOMAIN=clipcrow.com

# ENGINE_CONFIG and MYSTUDIES_TEMPLATE do not need to be changed unless
# you have moved these files within your GitHub repo
export ENGINE_CONFIG=${GIT_ROOT}/deployment/deployment.hcl
export MYSTUDIES_TEMPLATE=${GIT_ROOT}/deployment/mystudies.hcl


#export auth_server_client_id=QFMrv678quwk7Maf