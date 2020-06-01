#!/bin/bash
# Script to copy AppId and OrgId from gcloud secret to CloudSQL.
set -e

# TODO: Change these to env variables.
SECRET_PROJECT=mystudies-demo-devops
DATA_PROJECT=mystudies-demo-data
SQL_IMPORT_BUCKET=mystudies-demo-data-sql-import
TMPFILE=$(mktemp)
ORG_NAME="Test Org"

# Write user registration server db name to TMPFILE.
echo "USE \`mystudies_userregistration\`;" >> ${TMPFILE}

# Read AppId and OrgId from secrets..
APP_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mobile-app-appid`
ORG_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mobile-app-orgid`

# Write corresponding SQL commands to TMPFILE.
echo "INSERT INTO org_info (id, name, org_id) VALUES(1, \"${ORG_NAME}\", \"${ORG_ID}\");" >> ${TMPFILE}
echo "INSERT INTO app_info (app_info_id, custom_app_id, org_info_id) VALUES(1, \"${APP_ID}\", 1);" >> ${TMPFILE}
echo "INSERT INTO locations (id, is_default) VALUES(1, \"Y\");" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/mobile_app_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} my-studies ${GCS_FILE}
gsutil rm ${GCS_FILE}
