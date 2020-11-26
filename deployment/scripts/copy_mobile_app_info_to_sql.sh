# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to copy AppId from gcloud secret to CloudSQL.

#!/bin/bash
if [ "$#" -ne 3 ]; then
  echo 'Please provide deployment prefix and env in the order of <prefix> <env> <app_name>'
  exit 1
fi

PREFIX=${1}
ENV=${2}
APP_NAME=${3}
shift 2

set -e

SECRET_PROJECT=${PREFIX}-${ENV}-secrets
DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import
TMPFILE=$(mktemp)

# Write user registration server db name to TMPFILE.
echo "USE \`mystudies_participant_datastore\`;" >> ${TMPFILE}

# Read AppId from secrets.
APP_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=manual-mobile-app-appid`

# Write corresponding SQL commands to TMPFILE.
echo "REPLACE INTO app_info (id, custom_app_id, app_name) VALUES(1, \"${APP_ID}\", \"${APP_NAME}\");" >> ${TMPFILE}
echo "REPLACE INTO locations
  (id, custom_id, is_default, name, status)
VALUES
	('1', 'location1', 'Y', 'Site', 1);" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/mobile_app_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
