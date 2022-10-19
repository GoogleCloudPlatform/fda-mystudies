<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
The **FDA MyStudies** [`Participant datastore`](/participant-datastore/) is a set of three modules that provide APIs to store and manage participant registration, enrollment and consent. The [`Android`](/Android) and [`iOS`](/iOS) mobile applications interact with the `Participant datastore` to store and retrieve profile information specific to participants, and to establish enrollment and consent. The [`Response datastore`](/response-datastore/) interacts with the `Participant datastore` to determine enrollment state of participants. Pseudonymized study response data is stored in the `Response datastore` without participant identifiers (for example, answers to survey questions and activity data). Identifiable participant data is stored in the `Participant datastore` without study response data (for example, consent forms and participant contact information). This separation is designed to allow the deploying organization to configure distinct access control for each class of data. The `Participant datastore` uses basic authentication `client_id` and `client_secret` that are provided to client applications and managed by [`Hydra`](/hydra/).

The Participant datastore is composed of three applications that share a common database:

Module | Purpose | Client applications | Directory
---------------------|-----------------------------------------|-------------------|------------
`User module` | Maintains participant state and information | [`Study builder`](/study-builder/)<br/>[`Study datastore`](/study-datastore/)<br/>[`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/user-mgmt-module/](/participant-datastore/user-mgmt-module/)
`Enrollment module` | Maintains participant enrollment status  | [`Response datastore`](/response-datastore/)<br/>[`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/enroll-mgmt-module/](/participant-datastore/enroll-mgmt-module/)
`Consent module` | Maintains participant consent version status and provides access to generated consent documents | [`iOS application`](/iOS/)<br/>[`Android application`](/Android) | [/participant-datastore/consent-mgmt-module/](/participant-datastore/consent-mgmt-module/)
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

***
<p align="center">Copyright 2020 Google LLC</p>
