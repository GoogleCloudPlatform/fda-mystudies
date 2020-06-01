#!/bin/bash
# Script to copy client id and secret keys from gcloud secret to CloudSQL.
set -e

SECRET_PROJECT=mystudies-demo-devops
DATA_PROJECT=mystudies-demo-data
SQL_IMPORT_BUCKET=mystudies-demo-data-sql-import
TMPFILE=$(mktemp)

# Write auth server db name to TMPFILE.
echo "USE \`auth_server\`;" >> ${TMPFILE}

# Loop over all APP_CODE to read secret and write to TMPFILE.
i=0
for APP_CODE in MA URS RS WCP
do
  echo "Reading client id and secret key for: ${APP_CODE}"
  CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mystudies-${APP_CODE,,}-client-id`
  SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=mystudies-${APP_CODE,,}-secret-key`
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
