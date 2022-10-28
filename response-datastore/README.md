<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
The **FDA MyStudies** [`Response datastore`](/response-datastore/) provides APIs to store and manage pseudonymized study response and activity data. The `Response datastore` receives de-identified study responses from the [`Android`](/Android) and [`iOS`](/iOS) mobile applications, which are then written to a document-oriented database. Researchers and data scientists access this database to perform analysis of the study data, or for export to downstream systems. The `Response datastore` also receives de-identified activity data, which is written to a relational database. The application is built as a Spring Boot application. The backend database is Cloud Firestore for the response data, and MySQL for the activity data. The `Response datastore` uses basic authentication `client_id` and `client_secret`.

The `Response datastore` client applications are:
1. [`Android mobile application`](/Android/)
1. [`iOS mobile application`](/iOS/)
1. [`Study builder`](/study-builder)
1. [`Participant datastore`](/participant-datastore/)
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

***
<p align="center">Copyright 2020 Google LLC</p>
