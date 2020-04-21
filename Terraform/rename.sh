#!/bin/bash

# Disclaimer: This is originally for my own debugging and testing purposes. Use with caution.

export PWD=$(pwd)
export OUTPUT_PATH=/usr/local/google/home/xingao/gitrepos/demo

# Phase 1
# Use Engine to generate baseline configs. Need to git clone the dpt repo first, and copy the
# engine.tmpl.yaml to engine/samples/demo.yaml.
cd ~/gitrepos/src/github.com/GoogleCloudPlatform/healthcare/deploy && \
git checkout demo && \
rm -rf ${OUTPUT_PATH}/* && \
bazel run engine:main -- --config_path=engine/samples/demo.yaml --output_path=${OUTPUT_PATH}

cp -r /usr/local/google/home/xingao/gitrepos/fda-mystudies/Terraform/secrets ${OUTPUT_PATH}/

cd ${OUTPUT_PATH}

export OLD_STATE="heroes-hat-dev-terraform-state-08679"
export NEW_STATE="dpt-demo-042110-terraform-state"
export OLD_PREFIX="heroes-hat-dev"
export NEW_PREFIX="dpt-demo-042110"
export OLD_GKE_PREFIX="heroes-hat"
export NEW_GKE_PREFIX="dpt-demo-042110"
export OLD_FOLDER="fda-my-studies"
export NEW_FOLDER="dpt-demo-042110"
export SRC_BASE=/usr/local/google/home/xingao/gitrepos/fda-mystudies/Terraform/org/folder.${OLD_FOLDER}/project.${OLD_PREFIX}
export DST_BASE=${OUTPUT_PATH}/org/folder.${NEW_FOLDER}/project.${NEW_PREFIX}

# Phase 2
cp -r ${SRC_BASE}-networks/networks ${DST_BASE}-networks/
cp -r ${SRC_BASE}-data/data ${DST_BASE}-data/
cp -r ${SRC_BASE}-resp-firebase/firebase ${DST_BASE}-resp-firebase/

# Phase 3
cp -r ${SRC_BASE}-apps/apps ${DST_BASE}-apps/

# Phase 4
cp -r ${SRC_BASE}-apps/kubernetes ${DST_BASE}-apps/
cp -r ${SRC_BASE}-data/iam ${DST_BASE}-data/

# Globally unique resources or resources that cannot reuse the same name right after destroying.
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_STATE}|${NEW_STATE}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_PREFIX}|${NEW_PREFIX}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|${OLD_GKE_PREFIX}|${NEW_GKE_PREFIX}|"

export NEW_ORG_ID=
export NEW_BILLING_ACCOUNT=
export NEW_ADMIN_GROUP=

# Org info
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|707577601068|${NEW_ORG_ID}|"
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|01EA90-3519E1-89CB1F|${NEW_BILLING_ACCOUNT}|"

# Org group
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i "s|rocketturtle-gcp-admin@rocketturtle.net|${NEW_ADMIN_GROUP}|"

# Repo
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"GoogleCloudPlatform"|"xingao267"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"fda-mystudies"|"demo"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"terraform"|"master"|'
find . -type f -name *.tfvars -o -name *.tf | xargs sed -i 's|"early-access"|"master"|'

# Only needed if doing it again in the same org.
# find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|storage-org-sink|${NEW_PREFIX}-storage-org-sink|"
# find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl | xargs sed -i "s|bigquery-org-sink|${NEW_PREFIX}-bigquery-org-sink|"

cd ${PWD}
