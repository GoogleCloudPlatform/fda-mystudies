#!/bin/bash
# Script to copy client id and secret keys from gcloud secret to CloudSQL.
if [ "$#" -ne 2 ]; then
  echo 'Please provide deployment prefix and env in the order of <prefix> <env>>'
  exit 1
fi

PREFIX=${1}
ENV=${2}
shift 2

set -e

SECRET_PROJECT=${PREFIX}-${ENV}-secrets
DATA_PROJECT=${PREFIX}-${ENV}-data
SQL_IMPORT_BUCKET=${PREFIX}-${ENV}-mystudies-sql-import
SCIM_AUTH_URL="http://auth-server-np:50000"
HYDRA_ADMIN_URL="http://hydra-np:4445"
DATETIME=`date +"%FT%TZ"`

# SCIM AUTH SERVER, PARTICIPANT MANAGER & MOBILE APPS are registered
# with the same client_id and secret_key
AUTH_CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-auth-server-client-id`
AUTH_SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-auth-server-secret-key`

for CLIENT_NAME in "SCIM AUTH SERVER" "PARTICIPANT MANAGER" "MOBILE APPS"
do
  echo "Register ${CLIENT_NAME} with Hydra"
  OUTPUT=`curl --location --request POST "${HYDRA_ADMIN_URL}/clients" \
  --header "Content-Type: application/json" \
  --header "Accept: application/json" \
  --data-raw "{
    \"client_id\": \"${AUTH_CLIENT_ID}\",
    \"client_name\": \"${CLIENT_NAME}\",
    \"client_secret\": \"${AUTH_SECRET_KEY}\",
    \"client_secret_expires_at\": 0,
    \"created_at\": \"${DATETIME}\",
    \"grant_types\": [\"authorization_code\",\"refresh_token\",\"client_credentials\"],
    \"token_endpoint_auth_method\": \"client_secret_basic\",
    \"redirect_uris\": [\"${SCIM_AUTH_URL}/callback\"]
  }"`
  echo "${OUTPUT}"
end

# Loop over all applications to read secret and register with hydra.
for APPLICATION in participant-consent-datastore participant-enroll-datastore participant-manager-datastore participant-user-datastore response-datastore study-builder study-datastore
do
  CLIENT_NAME=`echo "${APPLICATION}" | tr '[:lower:]' '[:upper:]' | sed 's/-/ /'`
  echo "Reading client id and secret key for: ${CLIENT_NAME}"
  CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-${APPLICATION}-client-id`
  SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-${APPLICATION}-secret-key`

  echo "Register ${CLIENT_NAME} in Hydra"
  OUTPUT=`curl --location --request POST "${HYDRA_ADMIN_URL}/clients" \
--header "Content-Type: application/json" \
--header "Accept: application/json" \
--data-raw "{
  \"client_id\": \"${CLIENT_ID}\",
  \"client_name\": \"${CLIENT_NAME}\",
  \"client_secret\": \"${CLIENT_SECRET}\",
  \"client_secret_expires_at\": 0,
  \"created_at\": \"${DATETIME}\",
  \"grant_types\": [\"client_credentials\"],
  \"token_endpoint_auth_method\": \"client_secret_basic\",
  \"redirect_uris\": [\"${SCIM_AUTH_URL}/callback\"]
  }"`

  echo "${OUTPUT}"
done


