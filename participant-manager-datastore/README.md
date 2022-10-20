<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
The **FDA MyStudies** [`Participant manager datastore`](/participant-manager-datastore/) provides the backend APIs that the [`Participant manager`](/participant-manager/) web application uses to create and maintain participants, studies and sites. The `Participant manager datastore` is a Java Spring boot application that shares a MySQL backend database with the [`Participant datastore`](/participant-datastore/). The `Participant manager datastore` uses basic authentication `client_id` and `client_secret` that are provided to client applications and managed by [`Hydra`](/hydra/).
 
The `Participant manager datastore` client application is the [`Participant manager`](/participant-manager/) user interface. Interaction with other platform components is through the shared [`Participant datastore`](/participant-datastore/) database.
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

***
<p align="center">Copyright 2020 Google LLC</p>
