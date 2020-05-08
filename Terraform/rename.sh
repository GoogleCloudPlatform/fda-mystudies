#!/bin/bash

# Disclaimer: This is originally for my own debugging and testing purposes. Use with caution.

export PWD=$(pwd)

export INPUT_TF_BASE=/usr/local/google/home/xingao/gitrepos/fda-mystudies/Terraform
export OUTPUT_TF_BASE=/usr/local/google/home/xingao/gitrepos/demo

export OLD_STATE="heroes-hat-dev-terraform-state-08679"
export NEW_STATE="dpt-demo-terraform-state"
export OLD_PREFIX="heroes-hat-dev"
export NEW_PREFIX="dpt-demo"
export OLD_GKE_PREFIX="heroes-hat"
export NEW_GKE_PREFIX="dpt-demo"
export OLD_BIGQUERY_PREFIX="heroes_hat_dev"
export NEW_BIGQUERY_PREFIX="dpt_demo"
export OLD_FOLDER="fda-my-studies"
export NEW_FOLDER="dpt-demo"

export OLD_ORG_ID="707577601068"
export NEW_ORG_ID="18510592047"
export OLD_BILLING_ACCOUNT="01EA90-3519E1-89CB1F"
export NEW_BILLING_ACCOUNT="01B494-31B256-17B2A6"
export OLD_ADMIN_GROUP="rocketturtle-gcp-admin@rocketturtle.net"
export NEW_ADMIN_GROUP="dpt-dev@hcls.joonix.net"

export SRC_PROJ_BASE=${INPUT_TF_BASE}/org/folder.${OLD_FOLDER}/project.${OLD_PREFIX}
export DST_PROJ_BASE=${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}/project.${NEW_PREFIX}

# Cleanup output directory.
rm -rf ${OUTPUT_TF_BASE}/*

# Phase 1
for d in bootstrap secrets cicd kubernetes
do
  cp -r ${INPUT_TF_BASE}/$d ${OUTPUT_TF_BASE}/
done

mkdir -p ${OUTPUT_TF_BASE}/org
cp -r ${INPUT_TF_BASE}/org/README.md ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/terragrunt.hcl ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/audit ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/org_policies ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/project.${OLD_PREFIX}-audit ${OUTPUT_TF_BASE}/org/project.${NEW_PREFIX}-audit

mkdir -p ${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}
cp -r ${INPUT_TF_BASE}/org/folder.${OLD_FOLDER}/folder ${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}/

for d in networks data apps resp-firebase
do
  mkdir -p ${DST_PROJ_BASE}-$d
  cp -r ${SRC_PROJ_BASE}-$d/project ${DST_PROJ_BASE}-$d/
done

# Phase 2
cp -r ${SRC_PROJ_BASE}-networks/networks ${DST_PROJ_BASE}-networks/
cp -r ${SRC_PROJ_BASE}-data/data ${DST_PROJ_BASE}-data/
cp -r ${SRC_PROJ_BASE}-resp-firebase/firebase ${DST_PROJ_BASE}-resp-firebase/

# Phase 3
cp -r ${SRC_PROJ_BASE}-apps/apps ${DST_PROJ_BASE}-apps/

# Phase 4
cp -r ${SRC_PROJ_BASE}-apps/kubernetes ${DST_PROJ_BASE}-apps/
cp -r ${SRC_PROJ_BASE}-data/iam ${DST_PROJ_BASE}-data/

cd ${OUTPUT_TF_BASE}

# Globally unique resources or resources that cannot reuse the same name right after destroying.
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_STATE}|${NEW_STATE}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_PREFIX}|${NEW_PREFIX}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_GKE_PREFIX}|${NEW_GKE_PREFIX}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_BIGQUERY_PREFIX}|${NEW_BIGQUERY_PREFIX}|"

# Org info
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|${OLD_ORG_ID}|${NEW_ORG_ID}|"
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|${OLD_BILLING_ACCOUNT}|${NEW_BILLING_ACCOUNT}|"

# Org group
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|${OLD_ADMIN_GROUP}|${NEW_ADMIN_GROUP}|"

# Folder
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|${OLD_FOLDER}|${NEW_FOLDER}|"

# Repo
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"GoogleCloudPlatform"|"xingao267"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"fda-mystudies"|"demo"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"terraform"|"master"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"early-access"|"master"|'

cd ${PWD}
