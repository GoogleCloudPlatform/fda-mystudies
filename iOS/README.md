<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

![Build iOS](https://github.com/GoogleCloudPlatform/fda-mystudies/workflows/Build%20iOS/badge.svg) 
![SwiftLint](https://github.com/GoogleCloudPlatform/fda-mystudies/workflows/SwiftLint/badge.svg)

# Overview
This directory contains all the code necessary to build the **FDA MyStudies** iOS application for study participants. Customization of the [`Default.xcconfig`](MyStudies/MyStudies/Default.xcconfig) and [`Branding.plist`](MyStudies/MyStudies/Branding/Generic/Branding.plist) files will enable your iOS application to interact with the other components of your **FDA MyStudies** deployment. Further customization of app branding can be accomplished by replacing the default application images with your own. All configuration related to the creation and operation of studies is done using the [`Study builder`](../study-builder/) without need for code changes or redeployment of the mobile application.

<!--TODO A demonstration of the iOS mobile application can be found [here](todo). --->

![Example screens](../documentation/images/mobile-screens.png "Example screens")

# Requirements
The **FDA MyStudies** iOS application requires [Xcode 11](https://developer.apple.com/xcode/) or newer, and can be run on iOS versions 13 and above.

# Platform integration
The **FDA MyStudies** mobile application fetches all study, schedule, activity, eligibility, consent and notification information from the [`Study datastore`](../study-datastore/) and posts pseudonymized participant response data to the [`Response datastore`](../response-datastore/). Consent forms and any other identifiable data is posted to the [`Participant datastore`](../participant-datastore/). Email and password authentication is handled by the MyStudies [`Auth server`](../auth-server/) using OAuth 2.0.

# Configuration instructions
1. Open the [`iOS/MyStudies/MyStudies.xcworkspace`](MyStudies/MyStudies.xcworkspace) in Xcode
1. Map your project’s [build configuration](https://help.apple.com/xcode/mac/current/#/dev745c5c974) to [`iOS/MyStudies/MyStudies/Default.xcconfig`](MyStudies/MyStudies/Default.xcconfig) ([instructions](https://help.apple.com/xcode/mac/current/#/deve97bde215?sub=devf0d495219))
1. Update the following in the [`Default.xcconfig`](MyStudies/MyStudies/Default.xcconfig) file:
    -    Update `STUDY_DATASTORE_URL` with your [`Study datastore`](../study-datastore) URL
    -    Update `RESPONSE_DATASTORE_URL` with your [`Response datastore`](../response-datastore/) URL
    -    Update `USER_DATASTORE_URL` with your [`User datastore`](../participant-datastore/user-mgmt-module/) URL
    -    Update `ENROLLMENT_DATASTORE_URL` with your [`Enrollment datastore`](../participant-datastore/enroll-mgmt-module/) URL
    -    Update `CONSENT_DATASTORE_URL` with your [`Consent datastore`](../participant-datastore/consent-mgmt-module/) URL
    -    Update `AUTH_URL` with your [`Auth server`](../auth-server/) URL
    -    Update `HYDRA_BASE_URL` with your [`Hydra server`](../hydra/) URL
    -    Update `HYDRA_CLIENT_ID` with the `client_id` you configured during [`Hydra`](/hydra/) deployment (the mobile applications share a `client_id` with each other, the `Auth server` and the `Participant manager`) 
    -    Update `API_KEY` with the `bundle_id` and `app_token` that you configured [`study-datastore/src/main/resources/authorizationResource.properties`](../study-datastore/src/main/resources/authorizationResource.properties) during [`Study datastore`](/study-datastore/) deployment with format `<value of ios.bundleid>:<value of ios.apptoken>`
    -    Update `APP_ID` variable with the `AppId` that will be configured by the study administrator in the [`Study builder`](../study-builder/) user interface
    -    Set `APP_TYPE` to either “gateway” or “standalone”
    -    Update `STUDY_ID` key with the `StudyId` configured by the study administrator in the [`Study builder`](../study-builder/) user interface (not required for *Gateway* applications)
1. Configure Firebase Cloud Messaging (FCM) for push notifications
    -   Go to the [Firebase console](https://console.firebase.google.com/) and select the project you configured for Cloud Firestore during [Response datastore](../response-datastore) deployment.
    -   [Register your app](https://firebase.google.com/docs/cloud-messaging/ios/client#register-app) with Firebase and download the `GoogleService-Info.plist`. Move this config file into the root of your Xcode project.
    -   Upload your APNs authentication key to Firebase.
    -   Add [Firebase SDKs](https://firebase.google.com/docs/cloud-messaging/ios/client#add-sdks) and [Initialize Firebase](https://firebase.google.com/docs/cloud-messaging/ios/client#initialize_firebase_in_your_app) in your app.
1. Configure your [`Participant datastore`](/participant-datastore/) instance to interface with your mobile application (skip this step if following the semi-automated [deployment guide](/deployment/README.md) - you will complete an automated version of this task when you return to that guide)
    -    Make a copy of the [`participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) and update the values to match your iOS configuration
    -   Optionally, configure the Android fields to match your Android configuration (not necessary if you are not configuring an Android application, or if you have already completed this step during Android configuration)
    -    Run your updated [`mystudies_app_info_update_db_script.sql`](../participant-datastore/sqlscript/mystudies_app_info_update_db_script.sql) script on the `mystudies_participant_datastore` database that you created during [`Participant datastore`](/participant-datastore/) deployment ([instructions](https://cloud.google.com/sql/docs/mysql/import-export/importing#importing_a_sql_dump_file))   
1. *Optional.* Customize images and text
    -    Replace icons and images in [`iOS/MyStudies/MyStudies/Assets/Assets.xcassets`](MyStudies/MyStudies/Assets/Assets.xcassets/)
    -    Update user-facing text in the [`iOS/MyStudies/MyStudies/Branding/Generic/Branding.plist`](MyStudies/MyStudies/Branding/Generic/Branding.plist) file, fields to consider include:
         -    `ProductTitleName` - Application name that is shown to the user
         -    `WebsiteButtonTitle` - Text of the link that is shown on the overview screen
         -    `WebsiteLink` - Destination of the link that is shown on the overview screen
         -    `TermsAndConditionURL` - Destination for the terms and conditions link
         -    `PrivacyPolicyURL` - Destination for the privacy policy link
         -    `NavigationTitleName` - The navigation bar title that is shown to users
    -    Update introductory information presented to users in the [`iOS/MyStudies/MyStudies/Utils/Resources/Plists/UI/GatewayOverview.plist`](MyStudies/MyStudies/Utils/Resources/Plists/UI/GatewayOverview.plist) file
    -    Additional resource documents can be made available to users by adding PDF files to [`iOS/MyStudies/MyStudies/Assets/OtherAssets/`](MyStudies/MyStudies/Assets/OtherAssets/) and creating a corresponding entry in [`iOS/MyStudies/MyStudies/Models/Resource/Resources.plist`](MyStudies/MyStudies/Models/Resource/Resources.plist)

# Building and deploying

Instructions for building and deploying iOS applications can be found [here](https://help.apple.com/xcode/mac/current/#/devdc0193470).

***
<p align="center">Copyright 2020 Google LLC</p>
