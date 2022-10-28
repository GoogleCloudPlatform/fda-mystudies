<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

# Overview
The **FDA MyStudies** [`Study datastore`](/study-datastore/) makes APIs available for client applications to obtain the content of studies configured with the [`Study builder`](/study-builder/) web application. For example, the [`iOS`](/iOS/) and [`Android`](/Android/) mobile applications interact with the `Study datastore` to obtain the study schedule and tasks. The `Study datastore` serves the content and configuration of your organization’s studies - it does not process participant data. The `Study datastore` is a Java application built on the Spring framework. The backend database is MySQL, which is shared with the `Study builder` web application. The `Study datastore` uses basic authentication `bundle_id` and `app_token` to authenticate calls from client applications.
 
The `Study datastore` client applications are:
1. [`Android mobile application`](/Android/)
1. [`iOS mobile application`](/iOS/)
1. [`Response datastore`](/response-datastore/)
 
The `Study datastore` provides the following functionality:
1. Serve study settings to client applications
1. Serve study eligibility and consent data to client applications
1. Serve study schedule to client applications
1. Serve study activities to client applications
1. Serve study status to client applications
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.

***
<p align="center">Copyright 2020 Google LLC</p>
