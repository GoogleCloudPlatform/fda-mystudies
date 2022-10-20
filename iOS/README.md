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
The **FDA MyStudies** iOS application requires [Xcode 13](https://developer.apple.com/xcode/) or newer, and can be run on iOS versions 13 and above.

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
    -    Set the boolean `IsStandaloneStudyApp` value to  “gateway” or “standalone” in  [`iOS/MyStudies/MyStudies/Branding/Generic/Branding.plist`](MyStudies/MyStudies/Branding/Generic/Branding.plist)
    -    Update `StandaloneStudyId` key in [`iOS/MyStudies/MyStudies/Branding/Generic/Branding.plist`](MyStudies/MyStudies/Branding/Generic/Branding.plist) with the `StudyId` configured by the study administrator in the [`Study builder`](../study-builder/) user interface (not required for *Gateway* applications)
1. If you haven't already created the Firebase project then, [add Firebase to your Xcode project](https://firebase.google.com/docs/ios/setup) and make sure that Google Analytics is enabled in your Firebase project:
    -    If you're creating a new Firebase project, enable Google Analytics during the project creation workflow.
    -    If you're using an existing Firebase project that doesn't have Google Analytics enabled, go to the [Integrations](https://console.firebase.google.com/project/_/settings/integrations) tab of your settings > Project settings to enable it.
    -    When you enable Google Analytics in your project, your Firebase apps are linked to Google Analytics data streams.
    -    Add a Firebase [configuration file](https://firebase.google.com/docs/ios/setup#add-config-file) into the root of your Xcode project for using firebase services.
1. Enable push notifications by creating [push notification certificates](https://help.apple.com/developer-account/#/dev82a71386a) in encrypted `.p12` format (for more information, visit [Establishing a Certificate-Based Connection to APNs](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_certificate-based_connection_to_apns))
1. Enable push notification by [token based connection](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_token-based_connection_to_apns) with an authentication token signing key, specified as a text file (with a .p8 file extension).
2. Enable [FCM based push notification](https://firebase.google.com/docs/cloud-messaging/ios/client#upload_your_apns_authentication_key) by uploading your APNs authentication key to Firebase.
3. *Optional.* Customize images and text
    -    Replace icons and images in [`iOS/MyStudies/MyStudies/Assets/Assets.xcassets`](MyStudies/MyStudies/Assets/Assets.xcassets/)
    -    Update user-facing text in the [`iOS/MyStudies/MyStudies/Branding/Generic/Branding.plist`](MyStudies/MyStudies/Branding/Generic/Branding.plist) file, fields to consider include:
         -    `ProductTitleName` - Application name that is shown to the user
         -    `WebsiteButtonTitle` - Text of the link that is shown on the overview screen
         -    `NavigationTitleName` - The navigation bar title that is shown to users
    -    Update introductory information presented to users in the [`iOS/MyStudies/MyStudies/Utils/Resources/Plists/UI/GatewayOverview.plist`](MyStudies/MyStudies/Utils/Resources/Plists/UI/GatewayOverview.plist) file
    -    Additional resource documents can be made available to users by adding PDF files to [`iOS/MyStudies/MyStudies/Assets/OtherAssets/`](MyStudies/MyStudies/Assets/OtherAssets/) and creating a corresponding entry in [`iOS/MyStudies/MyStudies/Models/Resource/Resources.plist`](MyStudies/MyStudies/Models/Resource/Resources.plist)

# Building and deploying

Instructions for building and deploying iOS applications can be found [here](https://help.apple.com/xcode/mac/current/#/devdc0193470).

***
<p align="center">Copyright 2022 Google LLC</p>
