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
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

***
<p align="center">Copyright 2020 Google LLC</p>
