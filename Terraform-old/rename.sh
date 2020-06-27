#!/bin/bash

# Disclaimer: This is originally for my own debugging and testing purposes. Use with caution.

export PWD=$(pwd)

export INPUT_TF_BASE=/home/platform_admin_btc/Mohan-Sat/validcare-mystudies/Terraform-old
export OUTPUT_TF_BASE=/home/platform_admin_btc/Mohan-Sat/validcare-mystudies/Terraform

export OLD_STATE="heroes-hat-dev-terraform-state-08679"
export NEW_STATE="validcare-research-terraform-state-09768"

export OLD_PREFIX="heroes-hat-dev"
export NEW_PREFIX="validcare-research"

export OLD_GKE_PREFIX="heroes-hat"
export NEW_GKE_PREFIX="validcare-research"

export OLD_BIGQUERY_PREFIX="heroes_hat_dev"
export NEW_BIGQUERY_PREFIX="validcare-research"

export OLD_FOLDER="fda-my-studies"
export NEW_FOLDER="fda-my-studies"

export OLD_ORG_ID="707577601068"
export NEW_ORG_ID="423192334367"

export OLD_FOLDER_ID="440087619763"
export NEW_FOLDER_ID="346263855056"

export OLD_BILLING_ACCOUNT="01EA90-3519E1-89CB1F"
export NEW_BILLING_ACCOUNT="00C4F7-942DBB-FE88B3"

export OLD_ADMIN_GROUP="rocketturtle-gcp-admin@rocketturtle.net"
export NEW_ADMIN_GROUP="vr-gcp-admins@validcare.com"

export OLD_GITHUB_ORG="GoogleCloudPlatform"
export NEW_GITHUB_ORG="validcare"

export OLD_GITHUB_REPO="fda-mystudies"
export NEW_GITHUB_REPO="validcare-mystudies"

export OLD_GITHUB_BRANCH="early-access"
export NEW_GITHUB_BRANCH="Prod-Release"

export SRC_PROJ_BASE=${INPUT_TF_BASE}/org/folder.${OLD_FOLDER}/project.${OLD_PREFIX}
export DST_PROJ_BASE=${OUTPUT_TF_BASE}/org/folder.${NEW_FOLDER}/project.${NEW_PREFIX}

export OLD_ADMIN_EMAIL="group:rocketturtle-gcp-admin@rocketturtle.net"
export NEW_ADMIN_EMAIL="group:vr-gcp-admins@validcare.com"

export OLD_DOMAIN="rocketturtle.net"
export NEW_DOMAIN="validcare.com"

export OLD_AUDIT_BUCKET="7yr-org-audit-logs-08679"
export NEW_AUDIT_BUCKET="7yr-org-audit-logs-09768"

export OLD_AUDIT_ST_SINK="storage-org-sink"
export NEW_AUDIT_ST_SINK="storage-org-sink"

export OLD_AUDIT_BQ_SINK="bigquery-org-sink"
export NEW_AUDIT_BQ_SINK="bigquery-org-sink"

export OLD_CLUSTER="heroes_hat_cluster"
export NEW_CLUSTER="validcare-research-cluster"

export OLD_APP_ORG="Test Org"
export NEW_APP_ORG="VC Org"

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
cp -r ${INPUT_TF_BASE}/org/cicd ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/org_policies ${OUTPUT_TF_BASE}/org/
cp -r ${INPUT_TF_BASE}/org/project.${OLD_PREFIX}-audit ${OUTPUT_TF_BASE}/org/project.${NEW_PREFIX}-audit

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
 cp -r ${SRC_PROJ_BASE}-resp-firebase/iam ${DST_PROJ_BASE}-resp-firebase/

 # Deployment Phase 3 - Uncomment after Phase 1, 2 are deployed
  cp -r ${SRC_PROJ_BASE}-apps/apps ${DST_PROJ_BASE}-apps/

# Deployment Phase 4 - Uncomment after Phase 1, 2, 3 are deployed
# cp -r ${SRC_PROJ_BASE}-data/data ${DST_PROJ_BASE}-data/
# cp -r ${SRC_PROJ_BASE}-data/iam ${DST_PROJ_BASE}-data/

cd ${OUTPUT_TF_BASE}

# Globally unique resources or resources that cannot reuse the same name right after destroying.
files=$(find . -type f -name *.tfvars -o -name *.tf -o -name *.hcl -o -name README.md -o -name copy_client_info_to_sql.sh -o -name copy_mobile_app_info_to_sql.sh)
for f in $files
do 
  sed -i "s|${OLD_STATE}|${NEW_STATE}|" $f
  sed -i "s|${OLD_PREFIX}|${NEW_PREFIX}|" $f
  sed -i "s|${OLD_GKE_PREFIX}|${NEW_GKE_PREFIX}|" $f
  sed -i "s|${OLD_BIGQUERY_PREFIX}|${NEW_BIGQUERY_PREFIX}|" $f

  # Org info
  sed -i "s|${OLD_ORG_ID}|${NEW_ORG_ID}|" $f
  sed -i "s|${OLD_BILLING_ACCOUNT}|${NEW_BILLING_ACCOUNT}|" $f
  sed -i "s|${OLD_FOLDER_ID}|${NEW_FOLDER_ID}|" $f
  sed -i "s|${OLD_ADMIN_EMAIL}|${NEW_ADMIN_EMAIL}|" $f
  sed -i "s|${OLD_DOMAIN}|${NEW_DOMAIN}|" $f

  # Org group
  sed -i "s|${OLD_ADMIN_GROUP}|${NEW_ADMIN_GROUP}|" $f

  # Folder
  sed -i "s|${OLD_FOLDER}|${NEW_FOLDER}|" $f

  # Repo
  sed -i "s|\"${OLD_GITHUB_ORG}\"|\"${NEW_GITHUB_ORG}\"|" $f
  sed -i "s|"${OLD_GITHUB_REPO}"|"${NEW_GITHUB_REPO}"|" $f
  sed -i "s|"${OLD_GITHUB_BRANCH}"|"${NEW_GITHUB_BRANCH}"|" $f

  # Audit 
  sed -i "s|${OLD_AUDIT_BUCKET}|${NEW_AUDIT_BUCKET}|" $f
  sed -i "s|${OLD_AUDIT_ST_SINK}|${NEW_AUDIT_ST_SINK}|" $f
  sed -i "s|${OLD_AUDIT_BQ_SINK}|${NEW_AUDIT_BQ_SINK}|" $f

  # Apps
  sed -i "s|${OLD_CLUSTER}|${NEW_CLUSTER}|" $f
  sed -i "s|${OLD_APP_ORG}|${NEW_APP_ORG}|" $f
done

# Cleanup
find . -name ".terraform" | xargs rm -rf

cd ${PWD}
