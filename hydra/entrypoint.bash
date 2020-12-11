#!/bin/bash

# Copyright 2020 Google LLC
#
# Use of this source code is governed by an MIT-style
# license that can be found in the LICENSE file or at
# https://opensource.org/licenses/MIT.
#
# Require ENVs:
# - BASE_URL: the base URL of your hydra deployment.
# - DSN: format: "mysql://${DB_USER?}:${DB_PASSWORD?}@tcp(${DB_PRIVATE_IP}:${DB_PORT})/${DB_NAME?}"

# Encryption support in database
# The system secret can only be set against a fresh database. Key rotation is currently not supported. This
# secret is used to encrypt the database and needs to be set to the same value every time the process (re-)starts.
# You can use /dev/urandom to generate a secret. But make sure that the secret must be the same anytime you define it.
# You could, for example, store the value somewhere.
export SECRETS_SYSTEM=${SYSTEM_SECRET}

# Points to database location
# mysql://user:pw@tcp(host:port)/database?someSetting=value&foo=bar
export DSN="mysql://${DB_USER}:${DB_PASS}@tcp(${DB_INSTANCE_URL}:3306)/${DB_NAME}?sql_notes=false&parseTime=true"

# issuer URL
export URLS_SELF_ISSUER="${HYDRA_PUBLIC_BASE_URL}"
# Login, consent, error app
export URLS_CONSENT="${AUTH_SERVER_BASE_URL}/auth-server/consent"
export URLS_LOGIN="${AUTH_SERVER_BASE_URL}/auth-server/login"
export URLS_ERROR="${AUTH_SERVER_BASE_URL}/auth-server/oauth2/error"

# Setup database for hydra.
cd /hydra
./hydra migrate sql --yes $DSN

# Start hydra
# use --dangerous-force-http because GCLB take care of https.
./hydra serve all ${SERVE_ALL_FLAG}
