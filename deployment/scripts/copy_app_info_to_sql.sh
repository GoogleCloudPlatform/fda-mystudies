# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to copy Push Notification info from gcloud secret to CloudSQL.

#!/bin/bash
if [ "$#" -ne 2 ]; then
  echo 'Please provide deployment prefix and env in the order of <prefix> <env>'
  exit 1
fi

PREFIX=${1}
ENV=${2}
shift 2

set -e

SECRET_PROJECT=${PREFIX}-${ENV}-secrets
DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import

TMPFILE=$(mktemp)

# Write user registration server db name to TMPFILE.
echo "USE \`mystudies_participant_datastore\`;" >> ${TMPFILE}

# Read AppId and OrgId from secrets..
ANDROID_BUNDLE_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-android-bundle-id`
ANDROID_SERVER_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-android-server-key`
IOS_BUNDLE_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-ios-bundle-id`
IOS_CERTIFICATE=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-ios-certificate`
IOS_CERTIFICATE_PASSWORD=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-ios-certificate-password`
APP_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-mobile-app-appid`

# Write corresponding SQL commands to TMPFILE.
echo "UPDATE app_info SET
  android_bundle_id=\"${ANDROID_BUNDLE_ID}\",
  android_server_key=\"${ANDROID_SERVER_KEY}\",
  ios_bundle_id=\"${IOS_BUNDLE_ID}\",
  ios_certificate=\"${IOS_CERTIFICATE}\",
  ios_certificate_password=\"${IOS_CERTIFICATE_PASSWORD}\"
WHERE custom_app_id=\"${APP_ID}\";" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/push_notification_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
