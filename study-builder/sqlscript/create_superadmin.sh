#!/bin/bash

# Copyright 2020-2021 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to generate sql that creates study builder superadmin.
# Run like:
# $ ./study-builder/sqlscripts/create_superadmin.sh <email> <password>
# then import the generated sb-superadmin.sql file created in your current directory into the database.

if [ "$#" -ne 2 ]; then
  echo 'Please provide Study Builder superadmin email and password in the order of <email> <password>'
  exit 1
fi

EMAIL="${1}"
PWD="${2}"
shift 2

set -e

TMPFILE=$(mktemp)

echo "Generating the query to create a superadmin user for 'study_builder'"
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
EXPIRY_DATETIME=`date -v -90d +"%F %T"`
else # linux
EXPIRY_DATETIME=`date -d -90days +"%F %T"`
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

export DEST=`pwd -P`
export OUTPUT="${DEST}/sb-superadmin.sql"

echo "Writing the results in ${OUTPUT}"
mv ${TMPFILE} ${OUTPUT}
echo "Import ${OUTPUT} into the database to inject your initial superadmin user."
