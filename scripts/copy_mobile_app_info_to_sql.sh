#!/bin/bash
# Script to copy AppId from gcloud secret to CloudSQL.
set -e

PREFIX=example
ENV=dev

SECRET_PROJECT=${PREFIX}-${ENV}-secrets
DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import
TMPFILE=$(mktemp)
ORG_NAME="Test Org"

# Write user registration server db name to TMPFILE.
echo "USE \`mystudies_userregistration\`;" >> ${TMPFILE}

# Read AppId from secrets.
APP_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-mobile-app-appid`

# Write corresponding SQL commands to TMPFILE.
echo "INSERT INTO app_info (app_info_id, custom_app_id, org_info_id) VALUES(1, \"${APP_ID}\", 1);" >> ${TMPFILE}
echo "INSERT INTO locations (id, is_default) VALUES(1, \"Y\");" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/mobile_app_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
