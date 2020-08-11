#!/bin/bash
# Script to copy Push Notification info from gcloud secret to CloudSQL.
set -e

# TODO: Change these to env variables.
SECRET_PROJECT=heroes-hat-dev-devops
DATA_PROJECT=heroes-hat-dev-data
SQL_IMPORT_BUCKET=heroes-hat-dev-data-sql-import

TMPFILE=$(mktemp)

# Write user registration server db name to TMPFILE.
echo "USE \`mystudies_userregistration\`;" >> ${TMPFILE}

# bundleID used for the Android App
ANDROID_BUNDLE_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=android-bundle-id`
# found under settings > cloud messaging in the android app defined in your firebase project.
ANDROID_SERVER_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=android-server-key`
# bundleID used to build and distribute the iOS App. 
IOS_BUNDLE_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=ios-bundle-id`
# certificate and password generated for APNs
IOS_CERTIFICATE=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=ios-certificate`
IOS_CERTIFICATE_PASSWORD=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=ios-certificate-password`

# Write corresponding SQL commands to TMPFILE.
echo "UPDATE app_info SET 
  android_bundle_id=\"${ANDROID_BUNDLE_ID}\",
  android_server_key=\"${ANDROID_SERVER_KEY}\",
  ios_bundle_id=\"${IOS_BUNDLE_ID}\",
  ios_certificate=\"${IOS_CERTIFICATE}\",
  ios_certificate_password=\"${IOS_CERTIFICATE_PASSWORD}\"
WHERE app_info_id=1;" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/push_notification_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} my-studies ${GCS_FILE}
gsutil rm ${GCS_FILE}
