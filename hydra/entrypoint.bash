#!/bin/bash

# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Require ENVs:
# - BASE_URL: the base URL of your deployment.
# - DSN: format: "mysql://${DB_USER?}:${DB_PASSWORD?}@tcp(${DB_PRIVATE_IP}:${DB_PORT})/${DB_NAME?}?sslmode=disable"

# Encryption support in database
# The system secret can only be set against a fresh database. Key rotation is currently not supported. This
# secret is used to encrypt the database and needs to be set to the same value every time the process (re-)starts.
# You can use /dev/urandom to generate a secret. But make sure that the secret must be the same anytime you define it.
# You could, for example, store the value somewhere.
export SECRETS_SYSTEM=${SYSTEM_SECRET}

# Points to database location
# mysql://user:pw@tcp(host:port)/database?someSetting=value&foo=bar
export DSN=mysql://${DB_USER}:${DB_PASS}@tcp(localhost:3306)/${DB_NAME}?sql_notes=false

# issuer URL
export URLS_SELF_ISSUER="${BASE_URL}/hydra"
# Login and consent app
export URLS_CONSENT="${BASE_URL}/oauth-scim-service/consent"
export URLS_LOGIN="${BASE_URL}/oauth-scim-service/login"

# Setup database for hydra.
cd /hydra
./hydra migrate sql --yes $DSN

# Start hydra
# use --dangerous-force-http because GCLB take care of https.
./hydra serve all --dangerous-force-http
