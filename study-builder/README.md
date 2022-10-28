<!--
 Copyright 2020-2021 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->
 
# Overview
The **FDA MyStudies** [`Study builder`](/study-builder/) is a web application that provides a no-code user interface for researchers, clinicians and other study administrators to create and launch studies. As study administrators make changes to their studies using the `Study builder`, those changes are propagated to the participant mobile applications without need for code changes or app updates. The `Study builder` configures the content of your organization’s studies - it does not process participant data. The `Study builder` is a Java application built on the Spring framework. The backend database is a MySQL database, which is shared with the [`Study datastore`](/study-datastore/). The `Study datastore` serves the study content and configuration to the study’s mobile applications, which study participants use to interact with the study. The `Study builder` application uses built-in authentication and authorization.
 
The `Study builder` provides the following functionality:
1. Study administrator registration, login and logout
1. Creation and configuration of new studies (including eligibility, eConsent, activities and schedule)
1. Assignment of studies to mobile applications
1. Editing content and configuration of existing studies
1. Starting, pausing and ending studies
1. Sending notifications to study participants
 
<!-- A detailed user-guide for how to configure your first study can be found [here](TODO) --->
 
<!--TODO A demonstration of the `Study builder` application can be found [here](todo). --->
 
Example screens:
![Example screens](../documentation/images/study-builder-screens.png "Example screens")
 
# Deployment
> **_NOTE:_** Holistic deployment of the **FDA MyStudies** platform with Terraform and infrastructure-as-code is the recommended approach to deploying this component. A step-by-step guide to semi-automated deployment can be found in the [`deployment/`](/deployment) directory.
> 
***
<p align="center">Copyright 2020 Google LLC</p>
