# Script to insert the first participant manager superadmin into auth server.
# Run like:
# $ ./scripts/create_participant_manager_superadmin.sh <email> <password>

#!/bin/bash

if [ "$#" -ne 2 ]; then
  echo 'Please provide email and password in the order of <email> <password>'
  exit 1
fi

EMAIL="${1}"
PWD="${2}"
shift 2

set -e

DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import

SALT=`printf "%s" uuidgen | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
CRYPT=`printf "%s%s" $SALT $PWD | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
HASH=`printf "%s" $CRYPT | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
DATE=`date -v +30d +"%F %T"`
TIMESTAMP=`date -d "$DATE" +'%s.%3N'`

TMPFILE=$(mktemp)

echo "REPLACE into users (id, app_id, email, status, temp_reg_id, user_id, user_info)
  values(
  \"8ad16a8c74f823a10174f82c9a300001\",
  \"PARTICIPANT MANAGER\",
  \"${EMAIL}\",
  0,
  \"bd676334dd745c6afaa6547f9736a4c4df411a3ca2c4f514070daae31008cd9d\",
  \"96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508\",
  \"{\"password\": {\"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\":${TIMESTAMP}}, \"password_history\": [{\"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\":${TIMESTAMP}}]}\");
" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/push_notification_info.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
