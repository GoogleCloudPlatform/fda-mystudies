# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Script to copy client ids and secret keys from gcloud secret and register them
# in Hydra.
# Run like:
# $ ./scripts/register_clients_in_hydra.sh <prefix> <env> <domain>
# domain should your env domain name, e.g. mydomainname.com

#!/bin/bash
if [ "$#" -ne 3 ]; then
  echo 'Please provide deployment prefix and env in the order of <prefix> <env> <domain>'
  exit 1
fi

PREFIX=${1}
ENV=${2}
DOMAIN=${3}
shift 3

set -e

SECRET_PROJECT=${PREFIX}-${ENV}-secrets
# used by client side applications
SCIM_AUTH_EXTERNAL_URL="participants.${PREFIX}-${ENV}.${DOMAIN}/auth-server"
# used in server to server calls
SCIM_AUTH_URL="http://auth-server-np:50000/auth-server"
HYDRA_ADMIN_URL="http://hydra-admin-np:50000"
DATETIME=`date +"%FT%TZ"`

echo "Reading client id and secret key for auth, participant manager and mobile apps"
# SCIM AUTH SERVER, PARTICIPANT MANAGER & MOBILE APPS are registered
# with the same client_id and secret_key
AUTH_CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-auth-server-client-id`
AUTH_SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-auth-server-secret-key`

OUTPUT="curl --location --request POST \"${HYDRA_ADMIN_URL}/clients\" \\
  --header \"Content-Type: application/json\" \\
  --header \"Accept: application/json\" \\
  --data-raw '{
    \"client_id\": \"${AUTH_CLIENT_ID}\",
    \"client_name\": \"SCIM AUTH SERVER\",
    \"client_secret\": \"${AUTH_SECRET_KEY}\",
    \"client_secret_expires_at\": 0,
    \"created_at\": \"${DATETIME}\",
    \"grant_types\": [\"authorization_code\",\"refresh_token\",\"client_credentials\"],
    \"token_endpoint_auth_method\": \"client_secret_basic\",
    \"redirect_uris\": [
      \"http://${SCIM_AUTH_EXTERNAL_URL}/callback\",
      \"https://${SCIM_AUTH_EXTERNAL_URL}/callback\",
      \"${SCIM_AUTH_URL}/callback\"
    ]
  }';
"

# Loop over all applications to read secret and register with hydra.
for APPLICATION in "participant-consent-datastore" "participant-enroll-datastore" "participant-manager-datastore" "participant-user-datastore" "response-datastore" "study-builder" "study-datastore"
do
  CLIENT_NAME=`echo "${APPLICATION}" | tr '[:lower:]' '[:upper:]' | sed 's/-/ /g'`
  echo "Reading client id and secret key for: ${CLIENT_NAME}"

  CLIENT_ID=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-${APPLICATION}-client-id`
  SECRET_KEY=`gcloud --project=${SECRET_PROJECT} secrets versions access latest --secret=auto-${APPLICATION}-secret-key`

  OUTPUT="${OUTPUT} curl --location --request POST \"${HYDRA_ADMIN_URL}/clients\" \\
  --header \"Content-Type: application/json\" \\
  --header \"Accept: application/json\" \\
  --data-raw '{
    \"client_id\": \"${CLIENT_ID}\",
    \"client_name\": \"${CLIENT_NAME}\",
    \"client_secret\": \"${SECRET_KEY}\",
    \"client_secret_expires_at\": 0,
    \"created_at\": \"${DATETIME}\",
    \"grant_types\": [\"client_credentials\"],
    \"token_endpoint_auth_method\": \"client_secret_basic\",
    \"redirect_uris\": [
      \"http://${SCIM_AUTH_EXTERNAL_URL}/callback\",
      \"https://${SCIM_AUTH_EXTERNAL_URL}/callback\",
      \"${SCIM_AUTH_URL}/callback\"
    ]
  }';"
done

HYDRA_POD=`kubectl get pods --no-headers -o custom-columns=":metadata.name" | grep hydra`

echo "Running registration commands in Hydra pod"
kubectl exec ${HYDRA_POD} -c hydra-ic -- bash -c "${OUTPUT}"
