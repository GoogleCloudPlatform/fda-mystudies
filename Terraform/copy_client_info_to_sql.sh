#!/bin/bash
# Script to copy client id and secret keys from gcloud secret to CloudSQL.
set -e

SECRET_PROJECT=heroes-hat-dev-devops
DATA_PROJECT=heroes-hat-dev-data
SQL_IMPORT_BUCKET=heroes-hat-dev-data-sql-import
TMPFILE=$(mktemp)

# Write auth server db name to TMPFILE.
echo "USE \`auth_server\`;" >> ${TMPFILE}

# Loop over all APP_CODE to read secret and write to TMPFILE.
i=0
for APP_CODE in MA URS RS WCP
do
  APP_CODE_LOWER=`echo "${APP_CODE}" | tr '[:upper:]' '[:lower:]'`
  echo "Reading client id and secret key for: ${APP_CODE}"
  CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mystudies-${APP_CODE_LOWER}-client-id`
  SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mystudies-${APP_CAPP_CODE_LOWERODE}-secret-key`
  echo "REPLACE INTO client_info (client_info_id,app_code,client_id,secret_key) VALUES(${i},\"${APP_CODE}\",\"${CLIENT_ID}\",\"${SECRET_KEY}\");" >> ${TMPFILE}
  ((i=i+1))
done

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/auth_server_client_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} my-studies ${GCS_FILE}
gsutil rm ${GCS_FILE}
