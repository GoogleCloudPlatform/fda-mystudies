#!/bin/bash

# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to insert study builder superadmin.
# Run like:
# $ ./scripts/create_study_builder_superadmin.sh <prefix> <env> <email> <password>

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

TMPFILE=$(mktemp)

echo "Inserting/updating superadmin user for 'study_builder'"
echo "USE \`fda_hphc\`;" >> ${TMPFILE}

# hash password with BCrypt, default strength of 10.
# Replace 2y with 2a to make the algorithm consistent with Spring Security.
HASH=`htpasswd -bnBC 10 "" "${PWD}" | tr -d ':\n' | sed 's/$2y/$2a/'`

# e.g. N8K7zYrc0F
TOKEN=`cat /dev/urandom | LC_ALL=C tr -dc 'a-zA-Z0-9' | fold -w 10 | head -n 1`
# e.g ja67Ll
ACCESS_CODE=`cat /dev/urandom | LC_ALL=C tr -dc 'a-z0-9' | fold -w 6 | head -n 1`
# e.g. 2018-01-18 14:36:41
DATETIME=`date +"%F %T"`
if [[ "$OSTYPE" == "darwin"* ]]; then
EXPIRY_DATETIME=`date -v +90d +"%F %T"`
else # linux
EXPIRY_DATETIME=`date -d +90days +"%F %T"`
fi

echo "DELETE FROM user_permission_mapping WHERE user_id=1;" >> ${TMPFILE}
echo "REPLACE into users
(
  user_id, first_name, last_name, email, password, role_id, created_by,
  created_date, modified_by, modified_date, status, access_code,
  accountNonExpired, accountNonLocked, credentialsNonExpired, password_expiry_datetime,
  security_token, token_expiry_date, token_used, force_logout, access_level)
VALUES
(
  1, 'Account', 'Manager', '${EMAIL}', '${HASH}', 1, 1,
  '${DATETIME}', 1, '${DATETIME}', 1, '${ACCESS_CODE}',
  1, 1, 1, '${EXPIRY_DATETIME}',
  '${TOKEN}', '${EXPIRY_DATETIME}', 0, 'N', 'SUPERADMIN');
" >> ${TMPFILE}

echo "INSERT INTO user_permission_mapping (user_id, permission_id) VALUES
	(1, 1),
	(1, 2),
	(1, 4),
	(1, 5),
	(1, 6),
	(1, 7),
	(1, 8);
" >> ${TMPFILE}

# Upload TMPFILE to GCS.
GCS_FILE=gs://${SQL_IMPORT_BUCKET}/study_builder_supreadmin.sql
echo "Copying the sql file to ${GCS_FILE}"
gsutil mv ${TMPFILE} ${GCS_FILE}

# Import the GCS file to CloudSQL.
echo "Importing ${GCS_FILE} to CloudSQL."
gcloud sql import sql --project=${DATA_PROJECT} mystudies ${GCS_FILE}
gsutil rm ${GCS_FILE}
