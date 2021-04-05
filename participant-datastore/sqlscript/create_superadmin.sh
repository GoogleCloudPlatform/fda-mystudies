#!/bin/bash

# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to insert the first participant manager superadmin into auth server.
# Script to generate sql that creates study builder superadmin.
# Run like:
# $ ./participant-datastore/sqlscripts/create_superadmin.sh <email> <password>
# then import the generated pm-superadmin.sql file created in your current directory into the database.

if [ "$#" -ne 2 ]; then
  echo 'Please provide Participant Manager superadmin email and password in the order of <email> <password>'
  exit 1
fi

EMAIL="${1}"
PWD="${2}"
shift 2

set -e

TMPFILE=$(mktemp)

echo "USE \`oauth_server_hydra\`;" >> ${TMPFILE}

SALT=`printf "%s" uuidgen | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`
HASH=`printf "%s%s" $SALT $PWD | iconv -t utf-8 | openssl dgst -sha512 | sed 's/^.* //'`

if [[ "$OSTYPE" == "darwin"* ]]; then
DATE=`date -v +30d +"%F %T"`
TIMESTAMP=`date -v +30d +"%s.%3N"`
else # linux
DATE=`date -d +30days +"%F %T"`
TIMESTAMP=`date -d +30days +"%s.%3N"`
fi

echo "Generating the query to create/replace superadmin user in 'oauth_server_hydra' database"
echo "REPLACE into users (id, app_id, email, status, temp_reg_id, user_id, user_info)
  VALUES
  ('8ad16a8c74f823a10174f82c9a300001',
  'PARTICIPANT MANAGER',
  '${EMAIL}',
  5,
  'bd676334dd745c6afaa6547f9736a4c4df411a3ca2c4f514070daae31008cd9d',
  '96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508',
  '{ \"password\": { \"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\": \"${TIMESTAMP}\",
     \"password_history\": [{\"hash\": \"${HASH}\", \"salt\": \"${SALT}\", \"expire_timestamp\":\"${TIMESTAMP}\"}]}
    }');
" >> ${TMPFILE}

echo "USE \`mystudies_participant_datastore\`;" >> ${TMPFILE}

echo "Generate the query to insert a default location"
echo "REPLACE INTO locations
  (id, custom_id, is_default, name, status)
VALUES
	('1', 'location1', 'Y', 'Site', 1);
" >> ${TMPFILE}

SECURITY_CODE=`cat /dev/urandom | LC_ALL=C tr -dc 'a-z0-9' | fold -w 64 | head -n 1 | sed 's/^.* //'`
echo "Generating the query to create/replace ur_admin_user record in 'mystudies_participant_datastore' database"
echo "REPLACE INTO ur_admin_user
  (id, created_by, email, first_name, location_permission, security_code, security_code_expire_date, status, super_admin, ur_admin_auth_id)
VALUES
  ('c9d30d67-0477-4a8c-8490-0fa1e0300bd0', '1', '${EMAIL}', 'Admin', 1, '${SECURITY_CODE}', '${DATE}', 1, b'1', '96494ebc2ae5ac344437ec19bfc0b09267a876015b277e1f6e9bfc871f578508');
" >> ${TMPFILE}

export DEST=`pwd -P`
export OUTPUT="${DEST}/pm-superadmin.sql"

echo "Writing the results in ${OUTPUT}"
mv ${TMPFILE} ${OUTPUT}
echo "Import ${OUTPUT} into the database to inject your initial superadmin user."
