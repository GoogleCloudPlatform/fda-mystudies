#!/bin/bash

# Disclaimer: This is originally for my own debugging and testing purposes. Use with caution.
export PATH="/usr/local/opt/gnu-sed/libexec/gnubin:$PATH"
export PWD=$(pwd)

export INPUT_TF_BASE=/Users/zjabbari/projects/demo/fda-mystudies/Terraform
export OUTPUT_TF_BASE=/Users/zjabbari/projects/demo/fda-mystudies/TerraformOutput

export OLD_STATE="heroes-hat-dev-terraform-state-08679"
# make sure the gcs bucket has been created prior to running terraform init
export NEW_STATE="mystudies-terraform-state-19763"

export OLD_PREFIX="heroes-hat-dev"
export NEW_PREFIX="mystudies-demo"

export OLD_GKE_PREFIX="heroes-hat"
export NEW_GKE_PREFIX="mystudies"

export OLD_BIGQUERY_PREFIX="heroes_hat_dev"
export NEW_BIGQUERY_PREFIX="mystudies-demo"

export OLD_FOLDER="fda-my-studies"
export NEW_FOLDER="mystudies-demo"

export OLD_ORG_ID="707577601068"
export NEW_ORG_ID="18510592047"

export OLD_FOLDER_ID="440087619763"
export NEW_FOLDER_ID="440087619763"

export OLD_BILLING_ACCOUNT="01EA90-3519E1-89CB1F"
export NEW_BILLING_ACCOUNT="01B494-31B256-17B2A6"

export OLD_ADMIN_GROUP="rocketturtle-gcp-admin@rocketturtle.net"
export NEW_ADMIN_GROUP="hcls-mystudies-owners@google.com"

export OLD_GITHUB_ORG="GoogleCloudPlatform"
export NEW_GITHUB_ORG="zohrehj"

export OLD_GITHUB_REPO="fda-mystudies"
export NEW_GITHUB_REPO=

export OLD_GITHUB_BRANCH="early-access"
export NEW_GITHUB_BRANCH=

export SRC_PROJ_BASE=${INPUT_TF_BASE}/org/folder.${OLD_FOLDER}/project.${OLD_PREFIX}
export DST_PROJ_BASE=${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}/project.${NEW_PREFIX}

export OLD_ADMIN_EMAIL="group:rocketturtle-gcp-admin@rocketturtle.net"
export NEW_ADMIN_EMAIL="group:dpt-dev@hcls.joonix.net"

export OLD_DOMAIN="rocketturtle.net"
export NEW_DOMAIN="hcls.joonix.net"

export OLD_AUDIT_BUCKET="7yr-org-audit-logs-08679"
export NEW_AUDIT_BUCKET="7yr-org-audit-logs-19763"

# Cleanup output directory.
rm -rf ${OUTPUT_TF_BASE}/*

mkdir -p ${OUTPUT_TF_BASE}

# Deployment Phase 1
# 1.1 Deploy bootstrap/, cicd/ and secrets/ folder manually first.
# 1.2 Create PR to check in the Phase 1 configs, and let CICD deploys the rest of Phase 1
for d in bootstrap cicd secrets kubernetes copy_client_info_to_sql.sh copy_mobile_app_info_to_sql.sh README.md
do
  cp -r ${INPUT_TF_BASE}/$d ${OUTPUT_TF_BASE}/
done

mkdir -p ${OUTPUT_TF_BASE}/org
cp -r ${INPUT_TF_BASE}/org/README.md ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/terragrunt.hcl ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/audit ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/org_policies ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/project.${OLD_PREFIX}-audit ${OUTPUT_TF_BASE}/org/project.${NEW_PREFIX}-audit
cp -r ${INPUT_TF_BASE}/org/cicd ${OUTPUT_TF_BASE}/org/

mkdir -p ${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}
cp -r ${INPUT_TF_BASE}/org/folder.${OLD_FOLDER}/folder ${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}/

for d in networks data apps resp-firebase
do
  mkdir -p ${DST_PROJ_BASE}-$d
  cp -r ${SRC_PROJ_BASE}-$d/project ${DST_PROJ_BASE}-$d/
done

# Deployment Phase 2 - Uncomment after Phase 1 is deployed
cp -r ${SRC_PROJ_BASE}-networks/networks ${DST_PROJ_BASE}-networks/
cp -r ${SRC_PROJ_BASE}-resp-firebase/firebase ${DST_PROJ_BASE}-resp-firebase/

# Deployment Phase 3 - Uncomment after Phase 1, 2 are deployed
# cp -r ${SRC_PROJ_BASE}-apps/apps ${DST_PROJ_BASE}-apps/

# Deployment Phase 4 - Uncomment after Phase 1, 2, 3 are deployed
# cp -r ${SRC_PROJ_BASE}-data/data ${DST_PROJ_BASE}-data/
# cp -r ${SRC_PROJ_BASE}-data/iam ${DST_PROJ_BASE}-data/

cd ${OUTPUT_TF_BASE}

# Globally unique resources or resources that cannot reuse the same name right after destroying.
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_STATE}|${NEW_STATE}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_PREFIX}|${NEW_PREFIX}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_GKE_PREFIX}|${NEW_GKE_PREFIX}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_BIGQUERY_PREFIX}|${NEW_BIGQUERY_PREFIX}|"

# Org info
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_ORG_ID}|${NEW_ORG_ID}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_BILLING_ACCOUNT}|${NEW_BILLING_ACCOUNT}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_FOLDER_ID}|${NEW_FOLDER_ID}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_ADMIN_EMAIL}|${NEW_ADMIN_EMAIL}|"
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_DOMAIN}|${NEW_DOMAIN}|"

# Org group
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_ADMIN_GROUP}|${NEW_ADMIN_GROUP}|"

# Folder
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_FOLDER}|${NEW_FOLDER}|"

# Repo
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|"${OLD_GITHUB_ORG}"|"${NEW_GITHUB_ORG}"|"
# find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|"${OLD_GITHUB_REPO}"|"${NEW_GITHUB_REPO}"|"
# find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|"${OLD_GITHUB_BRANCH}"|"${NEW_GITHUB_BRANCH}"|"

# Audit
find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md | xargs sed -i "s|${OLD_AUDIT_BUCKET}|${NEW_AUDIT_BUCKET}|"

# Cleanup
find . -name ".terraform" | xargs rm -rf

cd ${PWD}
