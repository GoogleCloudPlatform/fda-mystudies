# Script to insert the first participant manager superadmin into auth server.
# Run like:
# $ ./scripts/create_participant_manager_superadmin.sh <prefix> <env> <email> <password>

#!/bin/bash

if [ "$#" -ne 4 ]; then
  echo 'Please provide deployment prefix and env, as well as superadmin email and password in the order of <prefix> <env> <email> <password>'
  exit 1
fi

PREFIX=${1}
ENV=${2}
EMAIL="${3}"
PWD="${4}"
shift 4

set -e

DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import

echo "Inserting/updating superadmin user in 'oauth_server_hydra' database"
echo "USE \`oauth_server_hydra\`;" >> ${TMPFILE}

SALT=`printf "%s" uuidgen | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
HASH=`printf "%s%s" $SALT $PWD | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
DATE=`date -v +30d +"%F %T"`
TIMESTAMP=`date -d "$DATE" +'%s.%3N'`

TMPFILE=$(mktemp)

echo "REPLACE into users (id, app_id, email, status, temp_reg_id, user_id, user_info)
  VALUES
  (\"8ad16a8c74f823a10174f82c9a300001\",
  \"PARTICIPANT MANAGER\",
  \"${EMAIL}\",
  0,
  \"bd676334dd745c6afaa6547f9736a4c4df411a3ca2c4f514070daae31008cd9d\",
  \"96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508\",
  \"{\"password\": {\"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\":${TIMESTAMP}}, \"password_history\": [{\"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\":${TIMESTAMP}}]}\");
" >> ${TMPFILE}


echo "USE \`mystudies_participant_datastore\`;" >> ${TMPFILE}
echo "Insert default location"
echo "REPLACE INTO locations
  (id, custom_id, is_default, name, status)
VALUES
	(\"1\", \"location1\", \"Y\", \"Default Location\", 1);
" >> ${TMPFILE}

SECURITY_CODE=`cat /dev/urandom | tr -dc 'a-z0-9' | fold -w 64 | head -n 1 | sed 's/^.* //'`
echo "Inserting/updating ur_admin_user record in 'mystudies_participant_datastore' database"
echo "REPLACE INTO ur_admin_user
  (id, created_by, email, first_name, location_permission, security_code, security_code_expire_date, status, super_admin, ur_admin_auth_id)
VALUES
  (\"c9d30d67-0477-4a8c-8490-0fa1e0300bd0\", \"1\", \"${EMAIL}\", \"Admin\", 1, \"${SECURITY_CODE}\", \"${DATE}\", 1, b'1', \"96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508\");
" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/participant_manager_superadmin.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
