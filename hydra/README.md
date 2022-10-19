<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
**FDA MyStudies** uses [ORY Hydra](https://www.ory.sh/hydra/) as an [OAuth 2.0](https://oauth.net/2/) and [OpenID Connect](https://openid.net/connect/) (OIDC) *Certified&copy;* technology to facilitate secure token generation and management, and to support integration with diverse identity providers. The FDA MyStudies platform uses a [SCIM](https://en.wikipedia.org/wiki/System_for_Cross-domain_Identity_Management) [`Auth server`](../auth-server) to implement email and password login using the Hydra APIs. If desired, code modifications will enable a deploying organization to supplement or replace the `Auth server` with an OIDC-compliant identity provider of choice.
 
The [`Hydra server`](../hydra/) provides the following functionality:
1. Client credentials management (`client_id` and `client_secret`)
1. Client credentials validation
1. Token generation and management
1. Token introspection
1. OAuth 2.0 flows
 
The [`/hydra/Dockerfile`](./Dockerfile) builds a [Hydra container](https://github.com/ory/hydra), then starts Hydra using [`entrypoint.bash`](./entrypoint.bash). This entrypoint script sets all necessary environment variables and executes [`migrate`](https://www.ory.sh/hydra/docs/cli/hydra-migrate-sql/) to update the schema of the backend database. 
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory. The following instructions are provided in case manual deployment in a VM is required. Google Cloud infrastructure is indicated, but equivalent alternative infrastructure can be used as well. It is important for the deploying organization to consider the identity and access control choices made when configuring the selected services. If pursuing a manual deployment, a convenient sequence is [`hydra/`](/hydra)&rarr;[`auth-server/`](/auth-server/)&rarr;[`participant-datastore/`](/participant-datastore/)&rarr;[`participant-manager-datastore/`](/participant-manager-datastore/)&rarr;[`participant-manager/`](/participant-manager/)&rarr;[`study-datastore/`](/study-datastore/)&rarr;[`response-datastore/`](/response-datastore/)&rarr;[`study-builder/`](/study-builder/)&rarr;[`Android/`](/Android/)&rarr;[`iOS/`](/iOS/).
 
# Hydra client configuration
 
The FDA MyStudies platform components are configured with a `client_id` and `client_secret`.  The grant type for each component and example values are listed in the table below. The `Auth server`, `Participant manager`, `Android` and `iOS` applications share a single set of credentials. You are responsible for generating and managing the values of `client_secret`. You can set these values with `Hydra` by making a POST request:
 
```shell
 curl    --location --request POST ‘<HYDRA_ADMIN_BASE_URL>/clients’ \
         --header 'Content-Type: application/json' \
         --header 'Accept: application/json' \
         --data-raw '{
         "client_id": "<CLIENT_ID>",
         "client_name": "<CLIENT_NAME>",
         "client_secret": "<CLIENT_SECRET>",
         "client_secret_expires_at": 0,
         "grant_types": ["authorization_code","refresh_token","client_credentials"],
         "token_endpoint_auth_method": "client_secret_basic",
         "redirect_uris": ["<AUTH_SERVER_BASE_URL>/callback"] 
         }’
```
For example, *<HYDRA_ADMIN_BASE_URL>* could be `https://10.128.0.2:4445` and *<AUTH_SERVER_BASE_URL>* could be `https://10.128.0.3`. See [`/deployment/scripts/register_clients_in_hydra.sh`](/deployment/scripts/register_clients_in_hydra.sh) for an example for how to create these resources efficiently.
 
Platform component | Grant type | client_id | client_name
----------------------------|---------------|---------------|-------------------
[`Participant datastore user module`](../participant-datastore/user-mgmt-module/) | `client_credentials` | `participant_user_datastore_hydra_client` | `participant_user_datastore`
[`Participant datastore enrollment module`](../participant-datastore/enroll-mgmt-module/) | `client_credentials` | `participant_enroll_datastore_hydra_client` | `participant_enroll_datastore`
[`Participant datastore consent module`](../participant-datastore/consent-mgmt-module/) | `client_credentials` | `participant_consent_datastore_hydra_client` | `participant_consent_datastore`
[`Participant manager datastore`](../participant-manager-datastore) | `client_credentials` | `participant_manager_datastore_hydra_client` | `participant_manager_datastore`
[`Study builder`](../study-builder/) | `client_credentials` | `study_builder_hydra_client` | `study_builder`
[`Study datastore`](../study-datastore/) | `client_credentials` | `study_datastore_hydra_client` | `study_datastore`
[`Auth server`](../auth-server/)<br/>[`Participant manager`](../participant-manager/)<br/>[`iOS mobile application`](../iOS/)<br/>[`Android mobile application`](../Android/) | `client_credentials`<br/>`refresh_token`<br/>`authorization_code` | `mystudies_hydra_client` | `mystudies`

***
<p align="center">Copyright 2020 Google LLC</p>
